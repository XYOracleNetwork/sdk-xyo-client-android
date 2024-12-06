package network.xyo.client.witness.location.info

import android.os.Bundle
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

// Extension function for safely retrieving typed values from a Bundle
private fun Bundle.getTypedValue(key: String): Any? {
    return when {
        containsKey(key) -> {
            when {
                getString(key) != null -> getString(key)
                getInt(key, Int.MIN_VALUE) != Int.MIN_VALUE -> getInt(key)
                getLong(key, Long.MIN_VALUE) != Long.MIN_VALUE -> getLong(key)
                getFloat(key, Float.MIN_VALUE) != Float.MIN_VALUE -> getFloat(key)
                getDouble(key, Double.MIN_VALUE) != Double.MIN_VALUE -> getDouble(key)
                getBoolean(key) -> getBoolean(key)
                else -> null
            }
        }
        else -> null
    }
}

@JsonClass(generateAdapter = true)
open class LocationPayloadRaw(
    val provider: String?,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float?,
    val bearing: Float?,
    val bearingAccuracyDegrees: Float?,
    val speed: Float?,
    val speedAccuracyMetersPerSecond: Float?,
    val verticalAccuracyMeters: Float?,
    val time: Long,
    val isMock: Boolean?,
    val extras: Map<String, Any?>? = null
): Payload(SCHEMA) {
    override fun dataHash(): String {
        return sha256String(this)
    }

    companion object {
        const val SCHEMA = "network.xyo.location.android"
        fun detect(
            provider: String?,
            latitude: Double,
            longitude: Double,
            altitude: Double,
            accuracy: Float?,
            bearing: Float?,
            bearingAccuracyDegrees: Float?,
            speed: Float?,
            speedAccuracyMetersPerSecond: Float?,
            verticalAccuracyMeters: Float?,
            time: Long,
            isMock: Boolean?,
            extras: Bundle? = null
        ): LocationPayloadRaw {
            val extrasMap = extras?.keySet()?.associateWith { key ->
                extras.getTypedValue(key)
            }

            return LocationPayloadRaw(
                provider,
                latitude,
                longitude,
                altitude,
                accuracy,
                bearing,
                bearingAccuracyDegrees,
                speed,
                speedAccuracyMetersPerSecond,
                verticalAccuracyMeters,
                time,
                isMock,
                extrasMap
            )
        }
    }
}