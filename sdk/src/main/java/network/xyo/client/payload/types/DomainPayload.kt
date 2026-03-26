package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for domain/URL information, matching JS @xyo-network/domain-payload-plugin.
 */
@JsonClass(generateAdapter = true)
open class DomainPayload(
    val domain: String? = null,
    val aliases: Map<String, Any?>? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.domain"
    }
}
