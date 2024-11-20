package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
open class XyoEventPayload(val event: String): XyoPayload() {
    override var schema = "network.xyo.event"
    val time_stamp = Date().time
}