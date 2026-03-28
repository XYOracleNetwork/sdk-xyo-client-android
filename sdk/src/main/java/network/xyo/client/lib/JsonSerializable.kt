package network.xyo.client.lib

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.types.Hash
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.security.MessageDigest

abstract class JsonSerializable: Serializable  {

    fun toJson(removeMeta: Boolean = false): String {
        return toJson(this, removeMeta)
    }

    companion object {

        /**
         * Field exclusion mode for canonical serialization.
         *
         * Per the XYO Yellow Paper:
         * - [NONE]: No fields excluded (raw serialization)
         * - [STORAGE_META]: Exclude top-level `_` prefix fields (for `hash()` computation)
         * - [ALL_META]: Exclude top-level `_` AND `$` prefix fields (for `dataHash()` computation)
         */
        enum class MetaExclusion {
            NONE,
            STORAGE_META,
            ALL_META
        }

        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        /**
         * Produce a canonical JSON string with sorted keys and optional field exclusion.
         * This is the primary serialization method — it builds the JSON string directly
         * to guarantee key ordering (JSONObject.toString() does not preserve order on JVM).
         */
        fun sortJson(json: String, exclusion: MetaExclusion = MetaExclusion.NONE): String {
            return canonicalJsonString(JSONObject(json), exclusion, depth = 0)
        }

        // Backward-compatible overload
        fun sortJson(json: String, removeMeta: Boolean): String {
            return sortJson(json, if (removeMeta) MetaExclusion.ALL_META else MetaExclusion.NONE)
        }

        /**
         * Build a canonical JSON string from a JSONObject with sorted keys.
         * Field exclusion only applies at the top level (depth == 0) per the Yellow Paper.
         *
         * Per the Yellow Paper Section 2.3 (removeEmptyFields):
         * - In JavaScript, `undefined` values are removed, `null` values are kept.
         * - In Kotlin, nullable fields serialized as JSON `null` by Moshi correspond
         *   to JavaScript's `undefined` (field not meaningfully set).
         * - JSON `null` as a direct object field value is removed (matches JS behavior
         *   where optional unset fields produce `undefined` which gets stripped).
         * - JSON `null` inside arrays is preserved (e.g., `previous_hashes: [null]`).
         */
        private fun canonicalJsonString(jsonObject: JSONObject, exclusion: MetaExclusion, depth: Int): String {
            val keys = jsonObject.keys().asSequence().sorted().filter { key ->
                if (depth == 0) {
                    when (exclusion) {
                        MetaExclusion.ALL_META -> !key.startsWith("_") && !key.startsWith("$")
                        MetaExclusion.STORAGE_META -> !key.startsWith("_")
                        MetaExclusion.NONE -> true
                    }
                } else {
                    true
                }
            }
            val entries = keys.mapNotNull { key ->
                val value = jsonObject.get(key)
                // Remove null object field values (equivalent to JS undefined removal).
                // Null inside arrays is preserved by canonicalArrayString.
                if (value == JSONObject.NULL) {
                    return@mapNotNull null
                }
                val valueStr = canonicalValueString(value, exclusion, depth + 1)
                "\"${escapeJsonString(key)}\":$valueStr"
            }
            return "{${entries.joinToString(",")}}"
        }

        /**
         * Build a canonical JSON string for an array.
         */
        private fun canonicalArrayString(jsonArray: JSONArray, exclusion: MetaExclusion, depth: Int): String {
            val elements = (0 until jsonArray.length()).map { i ->
                canonicalValueString(jsonArray.get(i), exclusion, depth)
            }
            return "[${elements.joinToString(",")}]"
        }

        /**
         * Build a canonical JSON string for any value.
         */
        private fun canonicalValueString(value: Any?, exclusion: MetaExclusion, depth: Int): String {
            return when (value) {
                is JSONObject -> canonicalJsonString(value, exclusion, depth)
                is JSONArray -> canonicalArrayString(value, exclusion, depth)
                JSONObject.NULL -> "null"
                null -> "null"
                is String -> "\"${escapeJsonString(value)}\""
                is Boolean -> value.toString()
                is Number -> jsonNumberString(value)
                else -> "\"${escapeJsonString(value.toString())}\""
            }
        }

        /**
         * Format a number to match JavaScript JSON.stringify behavior:
         * no trailing zeros, no positive sign, use shortest representation.
         */
        private fun jsonNumberString(value: Number): String {
            val d = value.toDouble()
            // If it's a whole number, render without decimal point
            if (d == d.toLong().toDouble() && d in Long.MIN_VALUE.toDouble()..Long.MAX_VALUE.toDouble()) {
                return d.toLong().toString()
            }
            return d.toString()
        }

        /**
         * Escape a string for JSON output per the JSON spec.
         */
        private fun escapeJsonString(s: String): String {
            val sb = StringBuilder(s.length)
            for (ch in s) {
                when (ch) {
                    '"' -> sb.append("\\\"")
                    '\\' -> sb.append("\\\\")
                    '\b' -> sb.append("\\b")
                    '\u000C' -> sb.append("\\f")
                    '\n' -> sb.append("\\n")
                    '\r' -> sb.append("\\r")
                    '\t' -> sb.append("\\t")
                    else -> {
                        if (ch < ' ') {
                            sb.append("\\u${String.format("%04x", ch.code)}")
                        } else {
                            sb.append(ch)
                        }
                    }
                }
            }
            return sb.toString()
        }

        // Legacy overloads that return JSONObject — kept for any code that depends on them
        fun sortJson(jsonObject: JSONObject, exclusion: MetaExclusion = MetaExclusion.NONE, depth: Int = 0): JSONObject {
            return JSONObject(canonicalJsonString(jsonObject, exclusion, depth))
        }

        fun sortJson(jsonObject: JSONObject, removeMeta: Boolean): JSONObject {
            return sortJson(jsonObject, if (removeMeta) MetaExclusion.ALL_META else MetaExclusion.NONE)
        }

        fun sortJson(jsonArray: JSONArray): JSONArray {
            return JSONArray(canonicalArrayString(jsonArray, MetaExclusion.NONE, 1))
        }

        fun toJson(obj: Any, exclusion: MetaExclusion): String {
            val adapter = moshi.adapter(obj.javaClass)
            return sortJson(adapter.toJson(obj), exclusion)
        }

        // Backward-compatible overload
        fun toJson(obj: Any, removeMeta: Boolean = false): String {
            return toJson(obj, if (removeMeta) MetaExclusion.ALL_META else MetaExclusion.NONE)
        }

        fun toJson(obj: List<Any>, removeMeta: Boolean = false): String {
            val adapter = moshi.adapter(obj.first().javaClass)
            val exclusion = if (removeMeta) MetaExclusion.ALL_META else MetaExclusion.NONE
            val items = obj.map { item -> sortJson(adapter.toJson(item), exclusion) }
            return items.joinToString(",", "[", "]")
        }

        fun <T: JsonSerializable>fromJson(json: String, obj: T): T? {
            val adapter = moshi.adapter(obj.javaClass)
            return adapter.fromJson(json)
        }

        fun sha256(value: String): Hash {
            val md = MessageDigest.getInstance("SHA256")
            md.update(value.encodeToByteArray())
            return md.digest()
        }

        @JvmStatic
        fun <T: JsonSerializable>sha256(obj: T, exclusion: MetaExclusion): Hash {
            val json = toJson(obj, exclusion)
            return sha256(json)
        }

        @JvmStatic
        fun <T: JsonSerializable>sha256(obj: T, removeMeta: Boolean = true): Hash {
            return sha256(obj, if (removeMeta) MetaExclusion.ALL_META else MetaExclusion.NONE)
        }

        @JvmStatic
        fun <T: JsonSerializable>sha256String(obj: T, exclusion: MetaExclusion): String {
            val shaBytes = sha256(obj, exclusion)
            return bytesToHex(shaBytes)
        }

        @JvmStatic
        fun <T: JsonSerializable>sha256String(obj: T, removeMeta: Boolean = true): String {
            return sha256String(obj, if (removeMeta) MetaExclusion.ALL_META else MetaExclusion.NONE)
        }

        private val hexArray = "0123456789abcdef".toCharArray()

        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF

                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }
}
