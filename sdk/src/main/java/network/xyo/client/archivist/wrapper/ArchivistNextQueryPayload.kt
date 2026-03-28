package network.xyo.client.archivist.wrapper

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Query payload for paginated retrieval from an archivist.
 * Per the XYO Yellow Paper Section 6.5.2.
 */
@JsonClass(generateAdapter = true)
class ArchivistNextQueryPayload(
    val cursor: String? = null,
    val limit: Int? = null,
    val open: Boolean? = null,
    val order: String? = null
) : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.query.archivist.next"
    }
}
