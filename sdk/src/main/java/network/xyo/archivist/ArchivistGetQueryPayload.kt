package network.xyo.archivist

import network.xyo.payload.JSONPayload

data class ArchivistGetQueryPayload(val hashes: Set<String>): JSONPayload(schema) {
    init {
        this.put("hashes", hashes)
    }

    companion object {
        const val schema = "network.xyo.query.archivist.get"
    }
}