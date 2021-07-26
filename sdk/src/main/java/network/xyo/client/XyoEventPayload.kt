package network.xyo.client

import java.util.*

open class XyoEventPayload(val event: String, previousHash: String?): XyoPayload("network.xyo.event", previousHash) {
    val time_stamp = Date().time
}