package network.xyo.chain.protocol.payload.elevatable

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Elevatable chain stake intent payload, matching JS ChainStakeIntent elevatable payload.
 */
@JsonClass(generateAdapter = true)
open class ChainStakeIntentPayload(
    val staked: String? = null,
    val amount: String? = null,
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.chain.stake.intent"
    }
}
