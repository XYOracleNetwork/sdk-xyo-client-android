package network.xyo.client.archivist.wrapper

import network.xyo.client.payload.XyoPayload

data class ArchivistInsertQueryPayload(val payloads: List<String>): XyoPayload("network.xyo.query.archivist.insert")