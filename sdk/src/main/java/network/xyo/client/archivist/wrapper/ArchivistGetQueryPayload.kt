package network.xyo.client.archivist.wrapper

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload

@JsonClass(generateAdapter = true)
open class ArchivistGetQueryPayload(val hashes: List<String>, schema: String = "network.xyo.query.archivist.get"): XyoPayload(schema)