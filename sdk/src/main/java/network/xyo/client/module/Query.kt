package network.xyo.client.module

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoPayload

@JsonClass(generateAdapter = true)
class Query(): Payload("network.xyo.query") {
    var address: String? = null
    var budget: Int? = null
    var maxFrequency: String? = null
    var minBid: Int? = null
}