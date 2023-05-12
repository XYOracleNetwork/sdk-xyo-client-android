package network.xyo.client.archivist.wrapper

import network.xyo.payload.Payload

class ArchivistInsertQueryPayload: Payload(schema) {
    companion object {
        const val schema = "network.xyo.query.archivist.insert"
    }
}