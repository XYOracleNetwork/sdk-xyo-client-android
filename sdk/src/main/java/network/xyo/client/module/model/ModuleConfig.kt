package network.xyo.client.module.model

/**
 * Configuration for a module instance, matching JS ModuleConfig.
 * Per the XYO Yellow Paper Section 10.5.
 */
interface ModuleConfig {
    /** The schema that identifies this module's configuration type. */
    val schema: String

    /** Optional human-readable name for this module. */
    val name: String?
        get() = null

    /** Restrict to only these query schemas. */
    val allowedQueries: List<String>?
        get() = null

    /** Name/address of archivist to use for storage. */
    val archivist: String?
        get() = null

    /** Key-value labels for factory matching. */
    val labels: Map<String, String>?
        get() = null

    /** Whether to sign every query. */
    val sign: Boolean?
        get() = null

    /** Whether to add timestamp payload to every query. */
    val timestamp: Boolean?
        get() = null

    companion object {
        const val SCHEMA = "network.xyo.module.config"
    }
}
