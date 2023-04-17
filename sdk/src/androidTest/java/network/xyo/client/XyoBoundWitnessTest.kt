package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.address.XyoAccount
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.archivist.api.XyoArchivistApiConfig
import network.xyo.client.boundwitness.QueryBoundWitnessBuilder
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoBoundWitnessTest {

    val apiDomainBeta = "https://beta.api.archivist.xyo.network"
    val apiDomainLocal = "http://10.0.2.2:8080"

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)


    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun testPayload1WithSend(apiDomain: String) {
        runBlocking {
            val account = XyoAccount()
            val client = NodeClient(apiDomainLocal, account)
            val query = XyoPayload("network.xyo.query.module.discover")
            val payloads = mutableListOf<XyoPayload>()
            payloads.add(TestPayload1())
            val postResult = client.query(query, payloads, null)
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
            val account = XyoAccount()
            val client = NodeClient(apiDomainLocal, account)
            val query = XyoPayload("network.xyo.query.module.discover")
            val payloads = mutableListOf<XyoPayload>()
            payloads.add(TestPayload2())
            val postResult = client.query(query, payloads, null)
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