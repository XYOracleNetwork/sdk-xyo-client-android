package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonClass
import network.xyo.client.XyoSerializable
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
                                Log.w("xyoClient", "lat: ${location.latitude}, long: ${location.longitude}")
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
                    latch.await(15, TimeUnit.SECONDS)

                    if (coordinates == null) {
                        return null
                    } else {
                        val currentLocation = CurrentLocation(coordinates!!, System.currentTimeMillis())
                        val serialized = XyoSerializable.toJson(currentLocation)
                        Log.i("xyoClient", "serialized currentLocation: ${serialized}")
                        return currentLocation
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        private fun setCoordinatesFromLocation(location: Location): Coordinates {
            Log.i("xyoClient", "Inside setCoordinatesFromLocation")

            val coordinates = Coordinates(
                location.accuracy,
                location.altitude,
                null,
                location.bearing,
                location.latitude,
                location.longitude,
                location.speed
            )
            val serialized = XyoSerializable.toJson(coordinates)
            Log.i("xyoClient", "serialized Coordinates: ${serialized}")
            return coordinates
        }
    }
}