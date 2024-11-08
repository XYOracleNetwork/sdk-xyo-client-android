package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonClass
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
class XyoLocationCurrent {
    companion object {

        @SuppressLint("MissingPermission")
        fun detect(context: Context): CurrentLocation? {
            if (LocationPermissions.check((context)) && LocationPermissions.checkGooglePlayServices(context)) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                var coordinates: Coordinates? = null

                try {
                    val latch = CountDownLatch(1)

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                Log.w("xyoClient", "Location was found")
                                coordinates = setCoordinatesFromLocation(location)
                                // countDown to zero to lift the latch
                                latch.countDown()
                            } else {
                                // countDown to zero to lift the latch
//                                latch.countDown()
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
                        return CurrentLocation(coordinates!!, System.currentTimeMillis())
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        private fun setCoordinatesFromLocation(location: Location): Coordinates {
            return Coordinates(
                location.accuracy,
                location.altitude,
                null,
                location.bearing,
                location.latitude,
                location.longitude,
                location.speed
            )
        }
    }
}