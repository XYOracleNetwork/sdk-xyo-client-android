package network.xyo.client

import android.util.Log
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class XyoPayload(open val schema: String, open val previousHash: String? = null): XyoSerializable() {
    open fun validate(): Boolean {
        if (schema != schema.lowercase()) {
            Log.e(this.javaClass.canonicalName, "'schema' must be lowercase")
            return false
        }
        return true
    }
}