package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for identifier data, matching JS @xyo-network/id-payload-plugin.
 */
@JsonClass(generateAdapter = true)
open class IdPayload(
    val salt: String? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.id"
    }
}
