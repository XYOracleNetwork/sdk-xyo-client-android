package network.xyo.chain.protocol.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for token transfers, matching JS TransferPayload.
 */
@JsonClass(generateAdapter = true)
open class TransferPayload(
    val from: String? = null,
    val transfers: Map<String, String>? = null,
    val epoch: Long? = null,
    val context: Map<String, Any?>? = null,
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.transfer"
    }
}
