package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.xyo.client.account.Account
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.payload.XyoPayload
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

open class TestSubjectPayload: XyoPayload() {
    override var schema = "network.xyo.test"
}

class TestPayload1SubObject {
    var number_value = 2
    var string_value = "yo"
}

class TestPayload1: TestSubjectPayload() {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestPayload1SubObject()
    var string_field = "there"
}

class TestPayload2SubObject {
    var string_value = "yo"
    var number_value = 2
}

class TestPayload2: TestSubjectPayload() {
    var string_field = "there"
    var object_field = TestPayload2SubObject()
    var timestamp = 1618603439107
    var number_field = 1
}

class TestInvalidSchemaPayload: TestSubjectPayload() {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestPayload1SubObject()
    var string_field = "there"
}

val knownAddress = Account.fromPrivateKey(ByteArray(32) {index -> index.toByte()})
const val knownHash = "6e173bbfc0577ebde66b44b090316eca5ecad8ecdb5c51886211d805c769d2ea"

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

        // ensure the schema is properly serialized from the base class
        assert(payloadJsonString.contains("network.xyo.test"))

        val payloadMirrored = XyoSerializable.fromJson(payloadJsonString, TestPayload1())
        assertNotNull(payloadMirrored)
        if (payloadMirrored != null) {
            assertEquals(payload.schema, payloadMirrored.schema)
        }
    }


    @Test
    fun testRoundTripPanel() {
        val address = Account.fromPrivateKey("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592")
        val witness = XyoWitness(address, fun(_: Context, _: String?): XyoPayload {
            return BasicPayload()
        })

        CoroutineScope(Dispatchers.Main).launch {
            val response = arrayListOf(witness.observe(appContext))
            val payloads = response.mapNotNull { payload -> payload }
            val bwJson = XyoBoundWitnessBuilder()
                .payloads(payloads)
                .witnesses(arrayListOf(witness))
                .build()
            val bwJsonString = XyoSerializable.toJson(bwJson)
            val bwMirrored = XyoSerializable.fromJson(bwJsonString, bwJson)
            assertNotNull(bwMirrored)
        }
    }

    @Test
    fun testHashing() {
        val payload = TestConstants.debugPayload
        assertEquals(payload.hash(), TestConstants.debugPayloadHash)
    }
}