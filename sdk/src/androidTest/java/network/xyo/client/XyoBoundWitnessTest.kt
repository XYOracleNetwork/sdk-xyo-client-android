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

    val apiDomainBeta = "https://api.archivist.xyo.network"
    val apiDomainLocal = "http://10.0.2.2:80"
    val archive = "test"

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)


    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun testNotAuthenticated(apiDomain: String) {
        val config = XyoArchivistApiConfig(archive, apiDomain)
        val api = XyoArchivistApiClient.get(config)
        assertFalse(api.authenticated)
    }

    @Test
    fun testNotAuthenticatedLocal() {
        testNotAuthenticated(apiDomainLocal)
    }

    @Test
    fun testNotAuthenticatedBeta() {
        testNotAuthenticated(apiDomainBeta)
    }

    fun testPayload1WithSend(apiDomain: String) {
        runBlocking {
            val address = XyoAddress("test")
            val config = XyoArchivistApiConfig(archive, apiDomain)
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
    fun testPayload1WithSendLocal() {
        testPayload1WithSend(apiDomainLocal)
    }

    @Test
    fun testPayload1WithSendBeta() {
        testPayload1WithSend(apiDomainBeta)
    }

    fun testPayload2WithSend(apiDomain: String) {
        runBlocking {
            val address = XyoAddress("test")
            val config = XyoArchivistApiConfig(archive, apiDomain)
            val api = XyoArchivistApiClient.get(config)
            val bw =
                XyoBoundWitnessBuilder().witness(address).payload("network.xyo.test", TestPayload2())
            val bwJson = bw.build()
            assertEquals(knownHash, bwJson._hash)
            val postResult = api.postBoundWitnessAsync(bwJson)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun testPayload2WithSendLocal() {
        testPayload2WithSend(apiDomainLocal)
    }

    @Test
    fun testPayload2WithSendBeta() {
        testPayload2WithSend(apiDomainBeta)
    }
}