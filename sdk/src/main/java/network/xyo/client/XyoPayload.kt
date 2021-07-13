package network.xyo.client

import com.google.gson.Gson
import java.security.MessageDigest

open class XyoPayload(val schema: String, val previousHash: String? = null) {
    open fun sha256(): String {
        val md = MessageDigest.getInstance("SHA")
        val payloadString: String = Gson().toJson(this)
        md.update(payloadString.encodeToByteArray())
        return md.digest().toString()
    }
}