package network.xyo.client.archivist.wrapper

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Query payload for taking a point-in-time snapshot of an archivist.
 * Per the XYO Yellow Paper Section 6.5.2.
 */
@JsonClass(generateAdapter = true)
class ArchivistSnapshotQueryPayload : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.query.archivist.snapshot"
    }
}
