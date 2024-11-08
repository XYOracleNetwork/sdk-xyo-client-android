package network.xyo.client.witness.location.info

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

data class Coordinates(
    val accuracy: Float?,
    val altitude: Double?,
    val altitudeAccuracy: Double?,
    val heading: Float?,
    val latitude: Double,
    val longitude: Double,
    val speed: Float?
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
            val gpInstance = GoogleApiAvailability.getInstance()
            val available = gpInstance.isGooglePlayServicesAvailable(context)
            val googlePlayServicesAvailable = available == 1
            if (googlePlayServicesAvailable) {
                Log.e("xyoClient", "Google Play Service not installed")
                return null
            }

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Log.e("xyoClient", "ACCESS_FINE_LOCATION permission not allowed")
                return null
            }

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                Log.e("xyoClient", "ACCESS_FINE_LOCATION permission not allowed")
                return null
            }

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            var coordinates: Coordinates? = null

            try {
                val latch = CountDownLatch(1)

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            coordinates = Coordinates(
                                location.accuracy,
                                location.altitude,
                                null,
                                location.bearing,
                                location.latitude,
                                location.longitude,
                                location.speed
                            )
                            // countDown to zero to lift the latch
                            latch.countDown()
                        } else {
                            // countDown to zero to lift the latch
                            latch.countDown()
                            Log.e("xyoClient","Location not available")
                        }
                    }
                    .addOnFailureListener {
                        Log.e("xyoClient","Failed to get location: ${it.message}")
                    }
                // Wait for up to 5 seconds for the location
                latch.await(5, TimeUnit.SECONDS)

                if (coordinates == null) {
                    return null
                } else {
                    val currentLocation = CurrentLocation(coordinates!!, System.currentTimeMillis())
                    return XyoLocationPayload(currentLocation)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return null

        }
    }
}