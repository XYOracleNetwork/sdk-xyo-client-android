package network.xyo.client.boundwitness

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.payload.TestPayload1
import network.xyo.client.account.Account
import network.xyo.client.account.Wallet
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.node.client.DiscoverPayload
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.Payload
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

data class RequestDependencies(val client: NodeClient, val query: Payload, val payloads: List<Payload>)

class BoundWitnessTest {

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
        val payloads = mutableListOf<Payload>()
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
            val bw = BoundWitnessBuilder().signer(Account.random()).payloads(listOf(
                TestPayload1()
            )).build()
            assert(bw.__client == "android")
            assert(bw.__signatures.size == 1)
        }
    }

    @Test
    fun testBoundWitnessMetaSerialization() {
        runBlocking {
            val bw = BoundWitnessBuilder().signer(Account.random()).payloads(listOf(
                TestPayload1()
            )).build()
            val serializedBw = JsonSerializable.toJson(bw)
            val bwJson = JSONObject(serializedBw)
            assert(bwJson.get("\$client") == "android")
            assertNotNull(bwJson.get("\$signatures"))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testBoundWitnessPreviousHash() {
        runBlocking {
            val testAccount = Wallet.fromMnemonic("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about")
            val knownBw1DataHash = "cb8b63aaaa8da5763f3e62541421c48b9b2356b4b9da24f58359072b89549e66"
            val testPayload = TestPayload1()
            val bw = BoundWitnessBuilder().signer(testAccount).payloads(listOf(
                testPayload
            )).build()
            val calcDataHash = bw.dataHash().toHexString()
            val calcJson = bw.toJson()
            val calcDataJson = bw.toJson(true)
            val calcDataHash2 = JsonSerializable.sha256(calcDataJson).toHexString()
            assertEquals(calcDataHash, calcDataHash2)
            assertNotEquals(calcJson, calcDataJson)
            assertEquals(knownBw1DataHash, calcDataHash)
            val bw2 = BoundWitnessBuilder().signer(testAccount).payloads(listOf(
                TestPayload1()
            )).build()
            assertEquals(bw2.previous_hashes.first(), knownBw1DataHash)
            assertEquals(bw2.previous_hashes.first(), bw.dataHash().toHexString())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testBoundWitnessRoundTripToArchivist() {
        runBlocking {
            val client = ArchivistWrapper(NodeClient("$apiDomainBeta/Archivist", null, appContext))
            val testAccount = Account.random()
            val testPayload = TestPayload1()
            val bw = BoundWitnessBuilder().signer(testAccount).payloads(listOf(testPayload)).build()
            val bwJson: String = bw.toJson()
            println("bwJson-start")
            println(bwJson)
            println("bwJson-end")
            val queryResult = client.insert(listOf(testPayload))

            assert((queryResult.errors?.size ?: 0) == 0)
            assert(((queryResult.response?.payloads?.size ?: 0) > 0))
            assert(((queryResult.response?.payloads?.size ?: 0) == 2))

            val bwDataHash = bw.dataHash().toHexString()
            assert(((queryResult.response?.payloads?.filter { it.dataHash().toHexString() == bwDataHash})?.size == 1))

            val dataResult = client.get(listOf(bwDataHash))
            val dataResponse = dataResult.response?.rawResponse
            assert(dataResponse!!.contains(bwDataHash))

            val bwHash = bw.dataHash().toHexString()
            val result = client.get(listOf(bwHash))
            val response = result.response?.rawResponse
            assert(response!!.contains(bwHash))
        }
    }
}