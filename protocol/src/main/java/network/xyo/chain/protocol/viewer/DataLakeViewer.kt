package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider

/**
 * View of the chain's DataLake — persistent payload store.
 * Mirrors DataLakeViewerMethods in @xyo-network/xl1-protocol-lib.
 *
 * Returns raw field maps rather than typed Payload instances because the JS
 * wire schema is `PayloadZod.loose()` — arbitrary extra fields beyond
 * `schema` are intentionally preserved, and the base Kotlin `Payload` class
 * would drop them. Callers that know the concrete payload schema can
 * deserialize the map into a typed subclass via Moshi's adapter.
 */
interface DataLakeViewer : Provider {
    override val moniker: String get() = MONIKER

    /** Fetch raw payload field maps by their canonical hashes. */
    suspend fun get(hashes: List<String>): List<Map<String, Any?>>

    /**
     * Fetch the next page of payloads. Options are intentionally modeled as a
     * permissive map because the JS schema uses zod's `$loose` object — the
     * cursor/limit/order fields evolve independently of this SDK.
     */
    suspend fun next(options: Map<String, Any?> = emptyMap()): List<Map<String, Any?>>

    companion object {
        const val MONIKER = "DataLakeViewer"
    }
}
