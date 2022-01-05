package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import network.xyo.client.payload.XyoPayload
import network.xyo.client.payload.XyoValidationException
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class TestPayload1SubObject {
    var number_value = 2
    var string_value = "yo"
}

class TestPayload1: XyoPayload("network.xyo.test") {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestPayload1SubObject()
    var string_field = "there"
}

class TestPayload2SubObject {
    var string_value = "yo"
    var number_value = 2
}

class TestPayload2: XyoPayload("network.xyo.test") {
    var string_field = "there"
    var object_field = TestPayload2SubObject()
    var timestamp = 1618603439107
    var number_field = 1
}

class TestInvalidSchemaPayload: XyoPayload("network.xyo.Test") {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestPayload1SubObject()
    var string_field = "there"
}

const val knownHash = "472e426f32cce7c693e09703ac57c49de6463cf250acd73df95ba8cfc9448511"

class XyoPayloadTest {

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
    fun testRoundTripPayload() {
        val payload = TestPayload1()
        val payloadJsonString = XyoSerializable.toJson(payload)
        val payloadMirrored = XyoSerializable.fromJson(payloadJsonString, TestPayload1())
        assertNotNull(payloadMirrored)
        if (payloadMirrored != null) {
            assertEquals(payload.schema, payloadMirrored.schema)
        }
    }

    @Test
    fun testRoundTripPanel() {
        val apiDomain = "https://beta.archivist.xyo.network"
        val archive = "test"
        val witness = XyoWitness(fun(_context: Context, previousHash: String?): XyoPayload {
            return XyoPayload("network.xyo.basic", previousHash)
        })
        val panel = XyoPanel(appContext, archive, apiDomain, listOf(witness, XyoSystemInfoWitness()))
        val bwJson = panel.generateBoundWitnessJson()
        val bwJsonString = XyoSerializable.toJson(bwJson)
        val bwMirrored = XyoSerializable.fromJson(bwJsonString, bwJson)
        assertNotNull(bwMirrored)
        if (bwMirrored != null) {
            assertEquals(bwJson._payloads?.size, bwMirrored._payloads?.size)
        }
    }
}