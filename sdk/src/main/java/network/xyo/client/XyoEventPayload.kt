package network.xyo.client

import network.xyo.client.payload.Payload
import java.util.*

open class EventPayload(name: String): Payload("network.xyo.event") {
    init {
        this.put("time_stamp", Date().time)
        this.put("name", name)
    }
}