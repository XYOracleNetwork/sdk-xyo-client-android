package network.xyo.client

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload
import java.util.*

@JsonClass(generateAdapter = true)
open class XyoEventPayload(val event: String, previousHash: String?): XyoPayload("network.xyo.event", previousHash) {
    val time_stamp = Date().time
}