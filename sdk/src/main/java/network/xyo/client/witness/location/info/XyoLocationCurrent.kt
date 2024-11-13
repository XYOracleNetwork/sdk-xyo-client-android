package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.suspendCancellableCoroutine
import network.xyo.client.XyoSerializable
import kotlin.coroutines.resumeWithException

@JsonClass(generateAdapter = true)
class XyoLocationCurrent {
    companion object {

        @SuppressLint("MissingPermission")
        suspend fun detect(context: Context): CurrentLocation? {
            if (LocationPermissions.check((context)) && LocationPermissions.checkGooglePlayServices(context)) {
                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
                return suspendCancellableCoroutine { continuation ->
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location ->
                            val coordinates = setCoordinatesFromLocation(location)
                            val currentLocation = CurrentLocation(coordinates, System.currentTimeMillis())
                            // Resume the coroutine with the retrieved location
                            continuation.resumeWith(Result.success(currentLocation))
                        }
                        .addOnFailureListener { exception ->
                            // Resume the coroutine with an exception if the task fails
                            continuation.resumeWithException(exception)
                        }
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