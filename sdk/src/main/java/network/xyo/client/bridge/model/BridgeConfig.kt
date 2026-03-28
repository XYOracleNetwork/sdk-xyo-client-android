package network.xyo.client.bridge.model

import network.xyo.client.module.model.ModuleConfig

/**
 * Bridge-specific configuration.
 * Per the XYO Yellow Paper Section 11.5.
 */
interface BridgeConfig : ModuleConfig {
    /** Client-side configuration. */
    val client: BridgeClientConfig?
        get() = null

    /** Host-side configuration. */
    val host: BridgeHostConfig?
        get() = null

    companion object {
        const val SCHEMA = "network.xyo.bridge.config"
    }
}

data class BridgeClientConfig(
    val discoverRoots: String? = null,
    val maxDepth: Int? = null
)

data class BridgeHostConfig(
    val maxDepth: Int? = null
)
