package network.xyo.client.witness.location.info

import android.content.Context
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
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
    currentLocation: CurrentLocation?
): XyoPayload() {
    override var schema: String = "network.xyo.location.android"

    companion object {
        fun detect(context: Context): XyoLocationPayload? {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != 1) {
                Log.e("xyoClient", "Google Play Service not installed")
                return null
            }

            return XyoLocationPayload(stubCurrentLocation)
        }
    }
}