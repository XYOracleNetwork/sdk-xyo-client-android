package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for generic values, matching JS @xyo-network/value-payload-plugin.
 */
@JsonClass(generateAdapter = true)
open class ValuePayload(
    val value: Any? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.value"
    }
}
