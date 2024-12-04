package network.xyo.client.lib

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.account.hexStringToByteArray
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.security.MessageDigest

abstract class XyoSerializable: Serializable  {

    companion object {

        fun sortJson(json: String, removeMeta: Boolean = false): String {
            return sortJson(JSONObject(json), removeMeta).toString()
        }

        fun sortJson(jsonObject: JSONObject, removeMeta: Boolean = false): JSONObject {
            val keys = jsonObject.keys().asSequence().sorted()
            val newJsonObject = JSONObject()
            keys.forEach {
                if (removeMeta) {
                    if (it.startsWith("_")) {
                        return@forEach
                    }
                }
                val value = jsonObject.get(it)
                if (value is JSONObject) {
                    newJsonObject.put(it, sortJson(value, removeMeta))
                } else if (value is JSONArray) {
                    newJsonObject.put(it, sortJson(value, removeMeta))
                } else {
                    newJsonObject.put(it, value)
                }
            }
            return newJsonObject
        }

        fun sortJson(jsonArray: JSONArray, removeMeta: Boolean = false): JSONArray {
            val newJsonArray = JSONArray()
            for (i in 0 until jsonArray.length()) {
                when (val value = jsonArray[i]) {
                    is JSONArray -> {
                        newJsonArray.put(sortJson(value, removeMeta))
                    }
                    is JSONObject -> {
                        newJsonArray.put(sortJson(value, removeMeta))
                    }
                    else -> {
                        newJsonArray.put(value)
                    }
                }
            }
            return newJsonArray
        }

        fun toJson(obj: Any, removeMeta: Boolean = false): String {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(obj.javaClass)
            return sortJson(adapter.toJson(obj), removeMeta)
        }

        fun toJson(obj: List<Any>, removeMeta: Boolean = false): String {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(obj.first().javaClass)
            val items = obj.map {item -> sortJson(adapter.toJson(item), removeMeta) }
            return items.joinToString(",", "[", "]")
        }

        fun <T: XyoSerializable>fromJson(json: String, obj: T): T? {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(obj.javaClass)
            return adapter.fromJson(json)
        }

        fun sha256(value: String): ByteArray {
            val md = MessageDigest.getInstance("SHA256")
            md.update(value.encodeToByteArray())
            return md.digest()
        }

        protected fun <T: XyoSerializable>sha256(obj: T): ByteArray {
            val json = toJson(obj, true)
            return sha256(json)
        }

        @JvmStatic
        protected fun <T: XyoSerializable>sha256String(obj: T): String {
            val shaBytes = sha256(obj)
            return bytesToHex(shaBytes)
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

fun String.hexToBytes(): ByteArray {
    return hexStringToByteArray(this)
}