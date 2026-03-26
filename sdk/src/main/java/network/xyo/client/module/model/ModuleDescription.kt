package network.xyo.client.module.model

/**
 * Discoverable metadata about a module, matching JS ModuleDescription.
 */
data class ModuleDescription(
    /** The module's address (hex string). */
    val address: String,

    /** Optional human-readable name. */
    val name: String? = null,

    /** List of query schemas this module supports. */
    val queries: List<String> = emptyList(),

    /** Child module addresses (for nodes). */
    val children: List<String> = emptyList()
)
