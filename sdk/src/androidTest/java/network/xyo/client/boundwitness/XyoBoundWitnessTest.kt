package network.xyo.client.boundwitness

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.payload.TestPayload1
import network.xyo.client.account.Account
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.node.client.DiscoverPayload
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import network.xyo.client.lib.XyoSerializable
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.json.JSONObject
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
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun setupAccount() {
        Account.previousHashStore = PreviousHashStorePrefsRepository.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    fun generateQuery(nodeUrl: String): RequestDependencies {
        val account = Account.random()
        val client = NodeClient(nodeUrl, account, appContext)
        val query = DiscoverPayload()
        val payloads = mutableListOf<XyoPayload>()
        payloads.add(TestPayload1())
        return RequestDependencies(client, query, payloads)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun testSendQueryBW(nodeUrl: String) {
        runBlocking {
            val(client, query, payloads) = generateQuery(nodeUrl)
            val postResult = client.query(query, payloads)
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
    fun testBoundWitnessMeta() {
        runBlocking {
            val bw = XyoBoundWitnessBuilder(appContext).signer(Account.random()).payloads(listOf(
                TestPayload1()
            )).build()
            assert(bw.dataHash() == bw.getBodyJson().dataHash())
            assert(bw.meta.client == "android")
            assert(bw.meta.signatures?.size == 1)
        }
    }

    @Test
    fun testBoundWitnessMetaSerialization() {
        runBlocking {
            val bw = XyoBoundWitnessBuilder(appContext).signer(Account.random()).payloads(listOf(
                TestPayload1()
            )).build()
            val serializedBw = XyoSerializable.toJson(bw)
            val bwJson = JSONObject(serializedBw)
            val meta = bwJson.get("\$meta") as JSONObject
            assert(meta.get("client") == "android")
            assertNotNull(meta.get("signatures"))
        }
    }

    @Test
    fun testBoundWitnessPreviousHash() {
        runBlocking {
            val testAccount = Account.random()
            val bw = XyoBoundWitnessBuilder(appContext).signer(testAccount).payloads(listOf(
                TestPayload1()
            )).build()
            val bw2 = XyoBoundWitnessBuilder(appContext).signer(testAccount).payloads(listOf(
                TestPayload1()
            )).build()
            assert(bw2.previous_hashes.first() == bw.dataHash())
        }
    }

    @Test
    fun testBoundWitnessRoundTripToArchivist() {
        runBlocking {
            val client = ArchivistWrapper(NodeClient("$apiDomainBeta/Archivist", null, appContext))
            val testAccount = Account.random()
            val testPayload = TestPayload1()
            val bw = XyoBoundWitnessBuilder(appContext).signer(testAccount).payloads(listOf(testPayload)).build()
            client.insert(listOf(bw, testPayload))

            val bwHash = bw.dataHash()
            val result = client.get(listOf(bwHash))
            val response = result.response?.rawResponse
            assert(response!!.contains(bwHash))
        }
    }
}