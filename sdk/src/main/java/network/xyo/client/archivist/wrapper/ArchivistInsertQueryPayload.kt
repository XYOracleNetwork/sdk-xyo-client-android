package network.xyo.client.archivist.wrapper

import network.xyo.client.payload.XyoPayload


class ArchivistInsertQueryPayload(): XyoPayload() {
    override var schema = ArchivistInsertQueryPayload.schema

    companion object {
        val schema = "network.xyo.query.archivist.insert"
    }
}