package network.xyo.chain.protocol.payload.elevatable

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Elevatable step completion payload, matching JS StepComplete elevatable payload.
 */
@JsonClass(generateAdapter = true)
open class StepCompletePayload(
    val step: Int? = null,
    val block: Long? = null,
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.step.complete"
    }
}
