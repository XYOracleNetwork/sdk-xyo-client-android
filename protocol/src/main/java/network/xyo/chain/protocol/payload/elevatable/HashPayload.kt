package network.xyo.chain.protocol.payload.elevatable

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Elevatable hash payload, matching JS Hash elevatable payload.
 */
@JsonClass(generateAdapter = true)
open class HashPayload(
    val hash: String? = null,
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.hash"
    }
}
