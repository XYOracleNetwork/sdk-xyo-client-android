package network.xyo.client.module.model

/**
 * Configuration for a module instance, matching JS ModuleConfig.
 */
interface ModuleConfig {
    /** The schema that identifies this module's configuration type. */
    val schema: String

    /** Optional human-readable name for this module. */
    val name: String?
        get() = null
}
