package network.xyo.archivist

import network.xyo.payload.JSONPayload

class ArchivistInsertQueryPayload: JSONPayload(schema) {
    companion object {
        const val schema = "network.xyo.query.archivist.insert"
    }
}