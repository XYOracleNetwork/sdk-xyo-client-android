package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import network.xyo.client.address.Account
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoPayload
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class TestXyoPayload1SubObject {
    var number_value = 2
    var string_value = "yo"
}

class XyoTestPayload1: XyoPayload("network.xyo.test") {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestXyoPayload1SubObject()
    var string_field = "there"
}

var TestPayload1 = Payload("network.xyo.test", mapOf(
    "timestamp" to 1618603439107,
    "number_field" to "1",
    "object_field" to mapOf(
        "number_value" to 2,
        "string_value" to "yo"
    ),
    "string_field" to "there"
))

class TestXyoPayload2SubObject {
    var string_value = "yo"
    var number_value = 2
}

class TestXyoPayload2: XyoPayload("network.xyo.test") {
    var string_field = "there"
    var object_field = TestXyoPayload2SubObject()
    var timestamp = 1618603439107
    var number_field = 1
}

var TestPayload2 = Payload("network.xyo.test", mapOf(
    "string_field" to "there",
    "object_field" to mapOf(
        "number_value" to 2,
        "string_value" to "yo"
    ),
    "timestamp" to 1618603439107,
    "number_field" to "1"
))

class TestInvalidSchemaXyoPayload: XyoPayload("network.xyo.Test") {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestXyoPayload1SubObject()
    var string_field = "there"
}

val knownAddress = Account(ByteArray(32) { index -> index.toByte()})
const val knownHash = "6e173bbfc0577ebde66b44b090316eca5ecad8ecdb5c51886211d805c769d2ea"

class PayloadTest {

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)

    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /*@Test
    fun testInvalidSchemaPayload() {
        val payload = TestInvalidSchemaPayload()
        assertThrows(XyoValidationException::class.java) {
            payload.validate()
        }
    }*/

    @Test
    fun testRoundTripXyoPayload() {
        val payload = XyoTestPayload1()
        val payloadJsonString = XyoSerializable.toJson(payload)
        val payloadMirrored = XyoSerializable.fromJson(payloadJsonString, XyoTestPayload1())
        assertNotNull(payloadMirrored)
        if (payloadMirrored != null) {
            assertEquals(payload.schema, payloadMirrored.schema)
        }
    }

    @Test
    fun testRoundTripPayload() {
        val payload = TestPayload1
        val payloadJsonString = payload.toString()
        val payloadMirrored = Payload.fromJson(payloadJsonString)
        assertNotNull(payloadMirrored)
        if (payloadMirrored != null) {
            assertEquals(payload.schema, payloadMirrored.schema)
        }
    }

    @Test
    fun testRoundTripPanel() {
        val address = Account(XyoSerializable.hexToBytes("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592"))
        val witness = XyoWitness(address, fun(_context: Context, previousHash: String?): XyoPayload {
            return XyoPayload("network.xyo.basic", previousHash)
        })
        val payloads = arrayListOf(witness.observe(appContext))
        val bwJson = XyoBoundWitnessBuilder()
            .payloads(payloads.mapNotNull { payload -> payload })
            .witnesses(arrayListOf(witness))
            .build()
        val bwJsonString = XyoSerializable.toJson(bwJson)
        val bwMirrored = XyoSerializable.fromJson(bwJsonString, bwJson)
        assertNotNull(bwMirrored)
        if (bwMirrored != null) {
            assertEquals(bwJson._payloads?.size, bwMirrored._payloads?.size)
        }
    }
}