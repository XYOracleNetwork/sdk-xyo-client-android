package network.xyo.client.archivist.wrapper

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

@JsonClass(generateAdapter = true)
class ArchivistInsertQueryPayload : Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.query.archivist.insert"
    }
}