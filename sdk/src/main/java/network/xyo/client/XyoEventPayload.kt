package network.xyo.client

import java.util.*

class XyoEventPayload: XyoPayload {
    val time_stamp = Date()
    val event: String

    constructor(event: String, previousHash: String?): super("network.xyo.event", previousHash) {
        this.event = event
    }
}