package network.xyo.client

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.MessageDigest

open class XyoSerializable {
    companion object {

        fun toJson(obj: Any): String {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val jc = obj.javaClass.canonicalName
            Log.d("XyoSerializable", jc)
            val adapter = moshi.adapter(obj.javaClass)
            val r = adapter.toJson(obj)
            Log.d("XyoSerializable2", r)
            return r
        }

        fun sha256(obj: Any): ByteArray {
            val md = MessageDigest.getInstance("SHA256")
            md.update(toJson(obj).encodeToByteArray())
            return md.digest()
        }

        fun sha256String(obj: Any): String {
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