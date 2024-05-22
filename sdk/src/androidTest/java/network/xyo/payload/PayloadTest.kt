package network.xyo.payload

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

var TestPayload1 = JSONPayload("network.xyo.test", mapOf(
    "timestamp" to 1618603439107,
    "number_field" to "1",
    "object_field" to mapOf(
        "number_value" to 2,
        "string_value" to "yo"
    ),
    "string_field" to "there"
))

@RunWith(AndroidJUnit4::class)
class PayloadTest {
    @Test
    fun testRoundTripPayload() {
        val payload = TestPayload1
        val payloadJsonString = payload.toString()
        val payloadMirrored = JSONPayload.from(payloadJsonString)
        assertNotNull(payloadMirrored)
        assertEquals(payload.schema, payloadMirrored.schema)
        assertEquals(payload.hash(), payloadMirrored.hash())
        assertEquals(payload.toString(), payloadMirrored.toString())
    }
}