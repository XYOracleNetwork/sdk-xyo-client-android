package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class DiscoverPayload(): XyoPayload() {
    override var schema = "network.xyo.query.module.discover"
}

class NodeClientTest {
    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"

    private lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun discoverTestBeta() {
        val account = Account.random()
        val client = NodeClient(TestConstants.nodeUrlBeta, account, appContext)
        val query = DiscoverPayload()

        runBlocking {
            val postResult = client.query(query, null)
            assertEquals(null, postResult.errors)
        }
    }

    /*
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun discoverTestLocal() {
        val account = Account.random()
        val client = NodeClient(TestConstants.nodeUrlBeta, account)
        val query = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val postResult = client.query(query, null, null)
            assertEquals(null, postResult.errors)
        }
    }
     */

    @Test
    fun archivistInsertTest() {
        val archivist = ArchivistWrapper(NodeClient(apiDomainBeta, TestConstants.TestAccount, appContext))

        val payloads = arrayListOf(TestConstants.debugPayload)

        runBlocking {
            val (response, errors) = archivist.insert(payloads)
            assertNotEquals(response, null)
            assertEquals(errors, null)

            if (response != null) {
                assertEquals(response.bw?.schema, "network.xyo.boundwitness")
            } else {
                throw(Error("Response should not be null"))
            }
        }

        runBlocking {
            val getResult = archivist.get(arrayListOf(TestConstants.debugPayloadHash))
            assertNotEquals(getResult.response, null)

            val response = getResult.response
            if (response != null) {
                assertEquals(response.payloads?.get(0)?.schema, TestConstants.debugPayload.schema)
            } else {
                throw(Error("Response should not be null"))
            }
        }
    }
}