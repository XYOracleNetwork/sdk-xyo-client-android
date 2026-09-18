package network.xyo.chain.protocol.payload.elevatable

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Elevatable time payload. Matches JS `network.xyo.time`: signed epoch milliseconds, with optional
 * XL1 and Ethereum height/hash anchors. `$epoch` on the block bound witness is unsigned client
 * meta and is not this clock.
 */
@JsonClass(generateAdapter = true)
open class TimePayload(
    val epoch: Long,
    val xl1: Long? = null,
    val xl1Hash: String? = null,
    val ethereum: Long? = null,
    val ethereumHash: String? = null,
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.time"
    }
}
