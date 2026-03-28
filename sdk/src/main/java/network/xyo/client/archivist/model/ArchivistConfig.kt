package network.xyo.client.archivist.model

import network.xyo.client.module.model.ModuleConfig

/**
 * Archivist-specific configuration.
 * Per the XYO Yellow Paper Section 11.1.
 */
interface ArchivistConfig : ModuleConfig {
    /** Parent archivists for read/write/commit delegation. */
    val parents: ArchivistParents?
        get() = null

    /** Whether to fail if some parents cannot be resolved. */
    val requireAllParents: Boolean?
        get() = null

    /** Whether to cache reads from parents locally. */
    val storeParentReads: Boolean?
        get() = null

    companion object {
        const val SCHEMA = "network.xyo.archivist.config"
    }
}

/**
 * Parent archivist references for delegation.
 */
data class ArchivistParents(
    val read: List<String>? = null,
    val write: List<String>? = null,
    val commit: List<String>? = null
)
