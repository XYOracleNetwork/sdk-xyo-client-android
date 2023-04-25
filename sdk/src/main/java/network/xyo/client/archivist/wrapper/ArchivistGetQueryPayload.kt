package network.xyo.client.archivist.wrapper

import network.xyo.client.payload.XyoPayload

data class ArchivistGetQueryPayload(val hashes: List<String>): XyoPayload("network.xyo.query.archivist.get")