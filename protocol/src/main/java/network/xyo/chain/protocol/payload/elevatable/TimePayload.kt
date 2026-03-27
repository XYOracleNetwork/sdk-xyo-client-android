package network.xyo.chain.protocol.payload.elevatable

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Elevatable time payload, matching JS Time elevatable payload.
 */
@JsonClass(generateAdapter = true)
open class TimePayload(
    val time: Long? = null,
    val domain: String? = null,
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.time"
    }
}
