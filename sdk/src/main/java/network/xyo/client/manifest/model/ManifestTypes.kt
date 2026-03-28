package network.xyo.client.manifest.model

/**
 * Manifest type definitions for declarative module graph instantiation.
 * Per the XYO Yellow Paper Section 15.
 */

/**
 * Configuration section of a module manifest.
 * Per the XYO Yellow Paper Section 15.3.
 */
data class ConfigManifest(
    /** Config schema (determines module type). */
    val schema: String,
    /** Module name (used for resolution). */
    val name: String,
    /** HD derivation path (e.g. "m/44'/60'/0'/0/0"). */
    val accountPath: String? = null,
    /** Feature flags. */
    val features: List<String>? = null,
    /** Labels for factory matching. */
    val labels: Map<String, String?>? = null,
    /** Implementation language. */
    val language: String? = null,
    /** Target OS. */
    val os: String? = null
)

/**
 * A single module's manifest.
 * Per the XYO Yellow Paper Section 15.3.
 */
data class ModuleManifest(
    /** Configuration for this module. */
    val config: ConfigManifest,
    /** If true, delay start until needed. */
    val lazyStart: Boolean? = null,
    /** Runtime status information. */
    val status: ModuleManifestStatus? = null
)

/**
 * Runtime status within a module manifest.
 */
data class ModuleManifestStatus(
    /** Current address (runtime). */
    val address: String,
    /** Address-to-name map of children. */
    val children: Map<String, String?>? = null
)

/**
 * A node manifest defines a Node module with its attached children.
 * Per the XYO Yellow Paper Section 15.2.
 */
data class NodeManifest(
    /** Configuration for this node. */
    val config: ConfigManifest,
    /** Child modules organized by visibility. */
    val modules: NodeManifestModules? = null,
    /** If true, delay start until needed. */
    val lazyStart: Boolean? = null,
    /** Runtime status information. */
    val status: ModuleManifestStatus? = null
)

/**
 * Child module organization within a node manifest.
 */
data class NodeManifestModules(
    /** Private children (not exposed to siblings). */
    val private: List<ModuleManifest>? = null,
    /** Public children (visible to sibling modules). */
    val public: List<ModuleManifest>? = null
)

/**
 * The top-level manifest that defines an entire application.
 * Per the XYO Yellow Paper Section 15.1.
 */
data class PackageManifest(
    /** Schema identifier. */
    val schema: String = SCHEMA,
    /** Reusable module templates by alias. */
    val modules: Map<String, ModuleManifest>? = null,
    /** Root-level nodes to instantiate. */
    val nodes: List<NodeManifest> = emptyList()
) {
    companion object {
        const val SCHEMA = "network.xyo.manifest.package"
    }
}
