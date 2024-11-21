package network.xyo.client.archivist.wrapper

import network.xyo.client.payload.XyoPayload

open class ArchivistGetQueryPayload(val hashes: List<String>): XyoPayload() {
    override var schema = ArchivistGetQueryPayload.schema

    companion object {
        val schema = "network.xyo.query.archivist.get"
    }
}