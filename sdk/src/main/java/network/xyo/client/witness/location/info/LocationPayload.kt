package network.xyo.client.witness.location.info

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

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
class LocationPayload(
    val currentLocation: CurrentLocation? = null,
): Payload(SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.location.current"

        fun detect(currentLocation: CurrentLocation?): LocationPayload {
            return LocationPayload(currentLocation)
        }
    }
}