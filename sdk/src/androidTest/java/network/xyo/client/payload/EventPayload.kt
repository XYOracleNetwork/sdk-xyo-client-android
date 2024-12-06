package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
open class EventPayload(val event: String): Payload(SCHEMA) {
    val timestamp = Date().time

    companion object {
        const val SCHEMA = "network.xyo.event"
    }
}