package network.xyo.client.witness.location.info

import android.content.Context
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload

data class Coordinates(
    val accuracy: Double?,
    val altitude: Double?,
    val altitudeAccuracy: Double?,
    val heading: Double?,
    val latitude: Double,
    val longitude: Double,
    val speed: Double?
)

data class CurrentLocation(
    val coords: Coordinates,
    val timestamp: Long
)

@JsonClass(generateAdapter = true)
class XyoLocationPayload (
    currentLocation: CurrentLocation
): XyoPayload() {
    override var schema: String = "network.xyo.location.android"

    companion object {
        fun detect(context: Context): XyoLocationPayload {
            val coordinates = Coordinates(
                accuracy = 10.0,
                altitude = 100.5,
                altitudeAccuracy = null,
                heading = 90.0,
                latitude = 40.7128,
                longitude = -74.0060,
                speed = null
            )
            val currentLocation = CurrentLocation(
                coords = coordinates,
                timestamp = System.currentTimeMillis()
            )
            return XyoLocationPayload(currentLocation)
        }
    }
}