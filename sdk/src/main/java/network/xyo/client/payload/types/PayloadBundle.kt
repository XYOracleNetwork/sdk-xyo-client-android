package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload bundle containing multiple payloads with a root reference.
 * Per the XYO Yellow Paper Section 1.5.
 */
@JsonClass(generateAdapter = true)
open class PayloadBundle(
    val root: String? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.payload.bundle"
    }
}
