package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.content.Context
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
class XyoLocationPayload (
    val currentLocation: CurrentLocation? = null
): XyoPayload() {
    override var schema: String = "network.xyo.location.android"

    companion object {

        @SuppressLint("MissingPermission")
        fun detect(context: Context): XyoLocationPayload? {
            return XyoLocationPayload(
                XyoLocationCurrent.detect(context)
            )
        }
    }
}