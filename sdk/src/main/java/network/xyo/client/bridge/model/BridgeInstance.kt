package network.xyo.client.bridge.model

import network.xyo.client.module.model.ModuleInstance

/**
 * Bridge module interface matching JS BridgeInstance.
 *
 * Bridges route queries between local and remote module systems.
 */
interface BridgeInstance : ModuleInstance {
    /** Get the list of module addresses exposed through this bridge. */
    suspend fun exposed(): List<String>

    /** Resolve a remote module by address through this bridge. */
    suspend fun resolveRemote(address: String): ModuleInstance?
}
