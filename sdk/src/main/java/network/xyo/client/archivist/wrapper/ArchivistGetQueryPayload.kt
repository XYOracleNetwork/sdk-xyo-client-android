package network.xyo.client.archivist.wrapper

import network.xyo.payload.Payload

data class ArchivistGetQueryPayload(val hashes: List<String>): Payload("network.xyo.query.archivist.get")