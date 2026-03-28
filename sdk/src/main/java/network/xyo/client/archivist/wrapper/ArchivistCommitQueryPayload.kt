package network.xyo.client.archivist.wrapper

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Query payload for committing pending changes to parent archivists.
 * Per the XYO Yellow Paper Section 6.5.2.
 */
@JsonClass(generateAdapter = true)
class ArchivistCommitQueryPayload : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.query.archivist.commit"
    }
}
