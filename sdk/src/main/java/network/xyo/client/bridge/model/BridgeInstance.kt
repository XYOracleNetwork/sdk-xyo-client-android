package network.xyo.client.bridge.model

import network.xyo.client.module.model.ModuleInstance

/**
 * Bridge module interface matching JS BridgeInstance.
 * Per the XYO Yellow Paper Section 11.5.
 *
 * Bridges route queries between local and remote module systems.
 */
interface BridgeInstance : ModuleInstance {
    /** Get the list of module addresses exposed through this bridge. */
    suspend fun exposed(): List<String>

    /** Resolve a remote module by address through this bridge. */
    suspend fun resolveRemote(address: String): ModuleInstance?

    /** Make a module available across the bridge. */
    suspend fun expose(id: String): List<ModuleInstance> {
        return emptyList()
    }

    /** Remove a module from the bridge. */
    suspend fun unexpose(id: String): List<String> {
        return emptyList()
    }

    /** Connect to a remote module. */
    suspend fun connect(id: String): String? {
        return null
    }

    /** Disconnect from a remote module. */
    suspend fun disconnect(id: String): String? {
        return null
    }
}
