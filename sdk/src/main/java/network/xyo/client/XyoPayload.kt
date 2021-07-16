package network.xyo.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.MessageDigest

open class XyoPayload(val schema: String, val previousHash: String? = null) {

    fun sha256(): String {
        val md = MessageDigest.getInstance("SHA256")
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(this.javaClass)
        val payloadString = adapter.toJson(this)
        md.update(payloadString.encodeToByteArray())
        return md.digest().toString()
    }
}