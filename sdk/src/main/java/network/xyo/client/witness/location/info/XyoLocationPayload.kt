package network.xyo.client.witness.location.info

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload

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
): XyoPayload() {
    override var schema: String
        get() = XyoLocationPayload.schema
        set(value) = Unit

    override fun hash(): String {
        return sha256String(this)
    }

    companion object {
        val schema = "network.xyo.location"
        fun detect(currentLocation: CurrentLocation?): XyoLocationPayload {
            return XyoLocationPayload(currentLocation)
        }
    }
}