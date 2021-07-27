package network.xyo.client

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.MessageDigest

open class XyoPayload(val schema: String, val previousHash: String? = null) {

    fun sha256(obj: Any): String {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(obj.javaClass)
        val jsonString = adapter.toJson(obj)
        val md = MessageDigest.getInstance("SHA256")
        md.update(jsonString.encodeToByteArray())
        val bytes: ByteArray = md.digest()
        return bytesToHex(bytes)
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