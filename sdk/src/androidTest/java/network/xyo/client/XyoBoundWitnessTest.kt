package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

data class RequestDependencies(val client: NodeClient, val query: XyoPayload, val payloads: List<XyoPayload>)

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

    fun generateQuery(nodeUrl: String): RequestDependencies {
        val account = Account.random()
        val client = NodeClient(nodeUrl, account)
        val query = DiscoverPayload()
        val payloads = mutableListOf<XyoPayload>()
        payloads.add(TestPayload1())
        return RequestDependencies(client, query, payloads)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun testSendQueryBW(nodeUrl: String) {
        runBlocking {
            val(client, query, payloads) = generateQuery(nodeUrl)
            val postResult = client.query(query, payloads, null)
            assertEquals(null, postResult.errors)
        }
    }

    /* @Test
    fun testSendQueryBWSendLocal() {
        testSendQueryBW(apiDomainLocal)
    }
    */

    @Test
    fun testSendQueryBWSendBeta() {
        testSendQueryBW(apiDomainBeta)
    }

    @Test
    fun testBoundWitnessHash() {
        runBlocking {
            val bw = XyoBoundWitnessBuilder().signer(Account.random()).payloads(listOf(TestPayload1())).build()
            val hashableFields = bw.getBodyJson()
            assert(bw._hash !== null)
            assert(bw._hash!! == XyoSerializable.sha256String(hashableFields))
            assert(bw._hash!! == hashableFields.hash())
        }
    }
}