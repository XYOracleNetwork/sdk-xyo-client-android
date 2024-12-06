package network.xyo.client.archivist.wrapper

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

@JsonClass(generateAdapter = true)
open class ArchivistGetQueryPayload(val hashes: List<String>): Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.query.archivist.get"
    }
}