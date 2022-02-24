package network.xyo.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.security.InvalidParameterException
import java.security.MessageDigest
import kotlin.experimental.or

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
                val value = jsonArray[i]
                if (value is JSONArray) {
                    newJsonArray.put(sortJson(value, removeMeta))
                }
                else if (value is JSONObject) {
                    newJsonArray.put(sortJson(value, removeMeta))
                } else {
                    newJsonArray.put(value)
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

        fun <T: XyoSerializable>sha256(obj: T): ByteArray {
            val json = toJson(obj, true)
            return sha256(json)
        }

        fun <T: XyoSerializable>sha256String(obj: T): String {
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

        fun hexToByte(hex: Char): Byte {
            return when(hex) {
                '0' -> 0
                '1' -> 1
                '2' -> 2
                '3' -> 3
                '4' -> 4
                '5' -> 5
                '6' -> 6
                '7' -> 7
                '8' -> 8
                '9' -> 9
                'a' -> 10
                'b' -> 11
                'c' -> 12
                'd' -> 13
                'e' -> 14
                'f' -> 15
                else -> throw(InvalidParameterException())
            }
        }

        fun hexToBytes(hex: String): ByteArray {
            val hexToConvert = hex.padStart(hex.length.mod(2), '0').lowercase()
            val byteCount = hexToConvert.length / 2
            val result = ByteArray(byteCount)
            for (i in 0 until byteCount) {
                result[i] = hexToByte(hexToConvert[i * 2]).rotateLeft(4) or hexToByte(hexToConvert[i * 2 + 1])
            }
            return result
        }
    }
}