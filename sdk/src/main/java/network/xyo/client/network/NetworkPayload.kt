package network.xyo.client.network

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload describing an XYO network node, matching JS @xyo-network/network.
 */
@JsonClass(generateAdapter = true)
open class NetworkNodePayload(
    val name: String? = null,
    val url: String? = null,
    val type: String? = null,
    val slug: String? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.network.node"
    }
}

/**
 * Payload describing an XYO network, matching JS @xyo-network/network.
 */
@JsonClass(generateAdapter = true)
open class NetworkPayload(
    val name: String? = null,
    val nodes: List<NetworkNodePayload>? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.network"

        /** Well-known XYO main network. */
        val MAIN = NetworkPayload(
            name = "Main",
            nodes = listOf(
                NetworkNodePayload(
                    name = "XYO Archivist",
                    url = "https://api.archivist.xyo.network",
                    type = "archivist"
                )
            )
        )

        /** Well-known XYO beta/test network. */
        val BETA = NetworkPayload(
            name = "Beta",
            nodes = listOf(
                NetworkNodePayload(
                    name = "XYO Archivist (Beta)",
                    url = "https://beta.api.archivist.xyo.network",
                    type = "archivist"
                )
            )
        )
    }
}
