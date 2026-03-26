package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for network addresses, matching JS @xyo-network/address-payload-plugin.
 */
@JsonClass(generateAdapter = true)
open class AddressPayload(
    val address: String? = null,
    val name: String? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.address"
    }
}
