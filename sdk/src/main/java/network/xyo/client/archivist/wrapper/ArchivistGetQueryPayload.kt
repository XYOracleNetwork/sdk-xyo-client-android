package network.xyo.client.archivist.wrapper

import network.xyo.client.payload.XyoPayload

open class ArchivistGetQueryPayload(val hashes: List<String>, schema: String = "network.xyo.query.archivist.get"): XyoPayload(schema)