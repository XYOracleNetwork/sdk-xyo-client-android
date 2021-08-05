package network.xyo.client

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.security.MessageDigest

abstract class XyoSerializable  {

    fun <T>adapter(): JsonAdapter<T> {
        val moshi = Moshi.Builder().build()
        return moshi.adapter<T>(this.javaClass)
    }

    companion object {

        fun <T: XyoSerializable>toJson(obj: T): String {
            val adapter = obj.adapter<T>()
            return adapter.toJson(obj)
        }

        fun <T: XyoSerializable>fromJson(json: String, obj: T): T? {
            val adapter = obj.adapter<T>()
            return adapter.fromJson(json)
        }

        fun sha256(value: String): ByteArray {
            val md = MessageDigest.getInstance("SHA256")
            md.update(value.encodeToByteArray())
            return md.digest()
        }

        fun <T: XyoSerializable>sha256(obj: T): ByteArray {
            return sha256(toJson(obj))
        }

        fun <T: XyoSerializable>sha256String(obj: T): String {
            return bytesToHex(sha256(obj))
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