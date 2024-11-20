package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.suspendCancellableCoroutine
import network.xyo.client.XyoSerializable
import kotlin.coroutines.resumeWithException

@JsonClass(generateAdapter = true)
class XyoLocationCurrent {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        suspend fun detect(context: Context): Pair<XyoLocationPayload, XyoLocationPayloadRaw>? {
            if (LocationPermissions.check((context)) && LocationPermissions.checkGooglePlayServices(context)) {
                val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
                return suspendCancellableCoroutine { continuation ->
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location === null) {
                                continuation.resumeWith(Result.success(null))
                                return@addOnSuccessListener
                            }

                            val locationRaw = buildRawLocationPayload(location)
                            val _sources = listOf(locationRaw.hash())
                            val coordinates = setCoordinatesFromLocation(location)
                            val currentLocation = CurrentLocation(coordinates, System.currentTimeMillis())

                            // Resume the coroutine with the retrieved location
                            continuation.resumeWith(Result.success(Pair(XyoLocationPayload(currentLocation, _sources), locationRaw)))
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
            Log.i("xyoClient", "serialized Coordinates: $serialized")
            return coordinates
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun buildRawLocationPayload(location: Location): XyoLocationPayloadRaw {
            return XyoLocationPayloadRaw.detect(
                location.provider,
                location.latitude,
                location.longitude,
                location.altitude,
                location.accuracy,
                location.bearing,
                location.bearingAccuracyDegrees,
                location.speed,
                location.speedAccuracyMetersPerSecond,
                location.verticalAccuracyMeters,
                location.time,
                handleIsMock(location),
                location.extras
            )
        }

        private fun handleIsMock(location: Location): Boolean? {
            // Conditionally figure out if using mocked location
           return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                location.isMock
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                location.isFromMockProvider
            } else {
                null
            }
        }
    }
}