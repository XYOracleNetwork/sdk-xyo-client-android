package network.xyo.client

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class XyoPayload(val schema: String, val previousHash: String? = null): XyoSerializable()