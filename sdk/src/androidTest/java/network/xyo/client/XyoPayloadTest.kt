package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

@JsonClass(generateAdapter = true)
class TestPayload1SubObject {
    var number_value = 2
    var string_value = "yo"
}

@JsonClass(generateAdapter = true)
class TestPayload1: XyoPayload("network.xyo.test") {
    var timestamp = 1618603439107
    var number_field = 1
    var object_field = TestPayload1SubObject()
    var string_field = "there"
}

@JsonClass(generateAdapter = true)
class TestPayload2SubObject {
    var string_value = "yo"
    var number_value = 2
}

@JsonClass(generateAdapter = true)
class TestPayload2: XyoPayload("network.xyo.test") {
    var string_field = "there"
    var object_field = TestPayload2SubObject()
    var timestamp = 1618603439107
    var number_field = 1
}

const val knownHash = "c9e112bce29ad33fdefe841c489640e9aa75ee6e721ede38de5999e6cf9035c7"

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

    @Test
    fun testRoundTripPayload() {
        val payload = TestPayload1()
        val moshi = Moshi.Builder()
            .build()
        val adapter = moshi.adapter(TestPayload1::class.java)
        val payloadJsonString = XyoSerializable.toJson(payload)
        val payloadMirrored = XyoSerializable.fromJson(payloadJsonString, payload)
        assertNotNull(payloadMirrored)
        if (payloadMirrored != null) {
            assertEquals(payload.schema, payloadMirrored.schema)
        }
    }
}