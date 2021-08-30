package network.xyo.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.security.MessageDigest

abstract class XyoSerializable: Serializable  {

    companion object {

        fun sortJson(json: String): String {
            return sortJson(JSONObject(json)).toString()
        }

        fun sortJson(jsonObject: JSONObject): JSONObject {
            val keys = jsonObject.keys().asSequence().sorted()
            val newJsonObject = JSONObject()
            keys.forEach {
                val value = jsonObject.get(it)
                if (value is JSONObject) {
                    newJsonObject.put(it, sortJson(value))
                } else if (value is JSONArray) {
                    newJsonObject.put(it, sortJson(value))
                } else {
                    newJsonObject.put(it, value)
                }
            }
            return newJsonObject
        }

        fun sortJson(jsonArray: JSONArray): JSONArray {
            val newJsonArray = JSONArray()
            for (i in 0 until jsonArray.length()) {
                val value = jsonArray[i]
                if (value is JSONArray) {
                    newJsonArray.put(sortJson(value))
                }
                else if (value is JSONObject) {
                    newJsonArray.put(sortJson(value))
                } else {
                    newJsonArray.put(value)
                }
            }
            return newJsonArray
        }

        fun toJson(obj: Any): String {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(obj.javaClass)
            return sortJson(adapter.toJson(obj))
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
            val json = toJson(obj)
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
    }
}