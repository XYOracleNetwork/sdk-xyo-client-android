package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

/**
 * A BoundWitness used for module queries.
 * Distinguished by the `query` field containing the dataHash of the query payload.
 * Per the XYO Yellow Paper Section 1.3.
 */
@JsonClass(generateAdapter = true)
open class QueryBoundWitness: BoundWitness() {
    /** The dataHash of the query payload. Included in data-hashable fields (gets signed). */
    var query: String? = null

    /** Hashes of error payloads, if any. */
    var error_hashes: List<String>? = null
}