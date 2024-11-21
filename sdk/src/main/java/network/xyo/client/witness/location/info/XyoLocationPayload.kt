package network.xyo.client.witness.location.info

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoPayload

interface XyoLocationPayloadMetaInterface : Payload {
    var _sources: List<String>?
}

@JsonClass(generateAdapter = true)
data class Coordinates(
    val accuracy: Float?,
    val altitude: Double?,
    val altitudeAccuracy: Double?,
    val heading: Float?,
    val latitude: Double,
    val longitude: Double,
    val speed: Float?
)

@JsonClass(generateAdapter = true)
data class CurrentLocation(
    val coords: Coordinates,
    val timestamp: Long
)

@JsonClass(generateAdapter = true)
class XyoLocationPayload(
    val currentLocation: CurrentLocation? = null,
    override var _sources: List<String>?
): XyoPayload(), XyoLocationPayloadMetaInterface {
    override var schema: String
        get() = payloadSchema
        set(value) = Unit

    override fun hash(): String {
        return sha256String(this)
    }

    companion object {
        val payloadSchema = "network.xyo.location"
        fun detect(currentLocation: CurrentLocation?, _sources: List<String>?): XyoLocationPayload {
            return XyoLocationPayload(currentLocation, _sources)
        }
    }
}