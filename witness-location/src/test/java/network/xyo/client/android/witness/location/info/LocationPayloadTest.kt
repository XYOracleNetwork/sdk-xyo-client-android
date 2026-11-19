package network.xyo.client.android.witness.location.info

import network.xyo.client.lib.JsonSerializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LocationPayloadTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `serializes and hashes LocationPayload and LocationPayloadRaw`() {
        val currentLocation = CurrentLocation(
            coords = Coordinates(
                accuracy = 5.0f,
                altitude = 42.0,
                altitudeAccuracy = null,
                heading = 180.0f,
                latitude = 32.7157,
                longitude = -117.1611,
                speed = 1.5f,
            ),
            timestamp = 1700000000000L,
        )
        val payload = LocationPayload.detect(currentLocation)
        assertEquals("network.xyo.location.current", payload.schema)
        val json = JsonSerializable.toJson(payload)
        assertTrue(json.contains("\"schema\":\"network.xyo.location.current\""))
        assertEquals(64, payload.dataHash().toHexString().length)

        val rawPayload = LocationPayloadRaw.detect(
            provider = "fused",
            latitude = 32.7157,
            longitude = -117.1611,
            altitude = 42.0,
            accuracy = 5.0f,
            bearing = 180.0f,
            bearingAccuracyDegrees = 2.0f,
            speed = 1.5f,
            speedAccuracyMetersPerSecond = 0.5f,
            verticalAccuracyMeters = 3.0f,
            time = 1700000000000L,
            isMock = false,
            extras = null,
        )
        assertEquals("network.xyo.location.android", rawPayload.schema)
        assertEquals(64, rawPayload.dataHash().toHexString().length)

        val witness: LocationWitness = XyoLocationWitness()
        assertNotNull(witness.address)
    }
}
