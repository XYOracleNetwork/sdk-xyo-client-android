package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Module error payload, matching JS ModuleError.
 * Per the XYO Yellow Paper Section 1.4.
 */
@JsonClass(generateAdapter = true)
open class ModuleErrorPayload(
    val message: String? = null,
    val name: String? = null,
    val query: String? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.error.module"
    }
}
