package network.xyo.client.module

import network.xyo.client.module.model.ModuleInstance

/**
 * Resolves module instances by address or name, matching JS ModuleResolver.
 */
interface ModuleResolver {
    /** Resolve a module by its address (hex string). */
    suspend fun resolveByAddress(address: String): ModuleInstance?

    /** Resolve a module by its name. */
    suspend fun resolveByName(name: String): ModuleInstance?

    /** Resolve modules matching the given query schema. */
    suspend fun resolveByQuery(schema: String): List<ModuleInstance>

    /** List all known modules. */
    suspend fun resolveAll(): List<ModuleInstance>
}
