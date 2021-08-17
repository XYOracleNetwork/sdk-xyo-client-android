package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.address.XyoAddress
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoBoundWitnessTest {

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
    fun testNotAuthenticated() {
        val config = XyoArchivistApiConfig("test", "http://localhost:3030/dev")
        val api = XyoArchivistApiClient.get(config)
        assertFalse(api.authenticated)
    }

    @Test
    fun testPayload1WithSend() {
        runBlocking {
            val address = XyoAddress("test")
            val config = XyoArchivistApiConfig("test", "https://beta.archivist.xyo.network")
            val api = XyoArchivistApiClient.get(config)
            val bw =
                XyoBoundWitnessBuilder().witness(address).payload("network.xyo.test", TestPayload1())
            val bwJson = bw.build()
            assertEquals(knownHash, bwJson._hash)
            val postResult = api.postBoundWitnessAsync(bwJson)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun testPayload2WithSend() {
        runBlocking {
            val address = XyoAddress("test")
            val config = XyoArchivistApiConfig("test", "https://beta.archivist.xyo.network")
            val api = XyoArchivistApiClient.get(config)
            val bw = XyoBoundWitnessBuilder().witness(address).payload("network.xyo.test", TestPayload2())
            val bwJson = bw.build()
            assertEquals(knownHash, bwJson._hash)
            val postResult = api.postBoundWitnessAsync(bwJson)
            assertEquals(null, postResult.errors)
        }
    }
}