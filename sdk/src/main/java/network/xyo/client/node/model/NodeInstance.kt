package network.xyo.client.node.model

import network.xyo.client.module.model.ModuleInstance

/**
 * Node module interface matching JS NodeInstance.
 * Per the XYO Yellow Paper Section 11.6.
 *
 * Nodes manage a hierarchy of modules, providing registration and discovery.
 */
interface NodeInstance : ModuleInstance {
    /** Attach a module to this node. Returns the attached module's address. */
    suspend fun attach(id: String, external: Boolean = false): String?

    /** Detach a module from this node. Returns the detached module's address. */
    suspend fun detach(id: String): String?

    /** List addresses of attached modules. */
    suspend fun attached(): List<String> {
        return emptyList()
    }

    /** List registered module addresses. */
    suspend fun registered(): List<String>

    /** Resolve a child module by address or name. */
    suspend fun resolve(id: String): ModuleInstance?

    /** Register a module with this node. */
    suspend fun register(module: ModuleInstance) {}

    /** Unregister a module from this node. */
    suspend fun unregister(module: ModuleInstance) {}
}
