package network.xyo.client.archivist.model

import network.xyo.client.module.model.ModuleInstance
import network.xyo.client.payload.Payload

/**
 * Archivist module interface matching JS ArchivistInstance.
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

    /** Get all stored payloads. */
    suspend fun all(): List<Payload>

    /** Clear all stored payloads. */
    suspend fun clear()
}
