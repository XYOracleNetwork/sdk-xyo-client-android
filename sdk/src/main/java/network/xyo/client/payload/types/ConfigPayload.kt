package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for configuration data, matching JS @xyo-network/config-payload-plugin.
 */
@JsonClass(generateAdapter = true)
open class ConfigPayload(
    val config: Map<String, Any?>? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.config"
    }
}
