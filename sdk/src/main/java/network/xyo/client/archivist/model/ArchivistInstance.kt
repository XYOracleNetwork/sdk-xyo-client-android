package network.xyo.client.archivist.model

import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.module.model.ModuleInstance
import network.xyo.client.payload.Payload

/**
 * Archivist module interface matching JS ArchivistInstance.
 * Per the XYO Yellow Paper Section 11.1.
 *
 * Archivists store and retrieve payloads.
 */
interface ArchivistInstance : ModuleInstance {
    /** Insert payloads into the archivist. Returns the stored payloads. */
    suspend fun insert(payloads: List<Payload>): List<Payload>

    /** Get payloads by their hashes. */
    suspend fun get(hashes: List<String>): List<Payload>

    /** Delete payloads by their hashes. Returns the deleted payloads. */
    suspend fun delete(hashes: List<String>): List<Payload>

    /** Get all stored payloads. Deprecated: use next() or snapshot() instead. */
    suspend fun all(): List<Payload>

    /** Clear all stored payloads. */
    suspend fun clear()

    /** Paginated retrieval of payloads. */
    suspend fun next(
        cursor: String? = null,
        limit: Int? = null,
        order: String? = null
    ): List<Payload> {
        return emptyList()
    }

    /** Commit pending changes to parent archivists. */
    suspend fun commit(): List<BoundWitness> {
        return emptyList()
    }

    /** Take a point-in-time snapshot of current state. */
    suspend fun snapshot(): List<Payload> {
        return emptyList()
    }
}
