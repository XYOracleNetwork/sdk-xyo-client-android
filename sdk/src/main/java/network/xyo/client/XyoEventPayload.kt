package network.xyo.client

import network.xyo.payload.JSONPayload
import java.util.*

open class EventPayload(name: String): JSONPayload("network.xyo.event") {
    init {
        this.put("time_stamp", Date().time)
        this.put("name", name)
    }
}