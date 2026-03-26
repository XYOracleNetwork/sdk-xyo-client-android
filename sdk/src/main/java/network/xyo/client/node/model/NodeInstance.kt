package network.xyo.client.node.model

import network.xyo.client.module.model.ModuleInstance

/**
 * Node module interface matching JS NodeInstance.
 *
 * Nodes manage a hierarchy of modules, providing registration and discovery.
 */
interface NodeInstance : ModuleInstance {
    /** Attach a module to this node. */
    suspend fun attach(module: ModuleInstance, external: Boolean = false)

    /** Detach a module from this node. */
    suspend fun detach(address: String)

    /** Get all registered (public) module addresses. */
    suspend fun registered(): List<String>

    /** Resolve a child module by address. */
    suspend fun resolve(address: String): ModuleInstance?
}
