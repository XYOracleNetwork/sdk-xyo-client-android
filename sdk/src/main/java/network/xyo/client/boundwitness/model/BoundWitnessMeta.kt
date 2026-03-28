package network.xyo.client.boundwitness.model

import java.io.Serializable

/**
 * Client metadata fields for BoundWitness ($ prefix).
 * These are included in `hash` but excluded from `dataHash`.
 * Per the XYO Yellow Paper Section 1.2.2.
 */
interface BoundWitnessMeta : Serializable {
    /** Compact ECDSA signatures, one per address. Serialized as `$signatures`. */
    val __signatures: List<String>

    /** Optional target address for directed messages. Serialized as `$destination`. */
    val __destination: String?
        get() = null

    /** Optional hash of the query that prompted this BoundWitness. Serialized as `$sourceQuery`. */
    val __sourceQuery: String?
        get() = null
}
