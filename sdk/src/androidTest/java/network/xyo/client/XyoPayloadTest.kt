package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONObject
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

const val knownHash = "817193f8fea1ac861bc9efd85e311c4f30820981d9f4c42924f769f1e4a23b1e"

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
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(TestPayload1::class.java)
        val payloadJsonString = adapter.toJson(payload)
        val jsonObject = JSONObject(payloadJsonString)
        val keys = jsonObject.keys().asSequence().sorted()
        val newJsonObject = JSONObject()
        keys.forEach {
            newJsonObject.put(it, jsonObject.get(it))
        }
        val newJsonObjectString = newJsonObject.toString()
        val payloadMirrored = adapter.fromJson(newJsonObjectString)
        assertNotNull(payloadMirrored)
        if (payloadMirrored != null) {
            assertEquals(payload.schema, payloadMirrored.schema)
        }
    }
}