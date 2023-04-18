package network.xyo.client

import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class NodeClientTest {

    private val apiDomainLocal = "http://10.0.2.2:8080"

    @Test
    fun discoverTest() {
        val account = XyoAccount()
        val client = NodeClient(apiDomainLocal, account)
        val query = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val postResult = client.query(query, null, null)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun archivistInsertTest() {
        val hash2 = "2967d5719b610e1dfa38651e8ff1a67d47b7bb4cea4ccfc8735b05d1660ef36f"
        val archivist = ArchivistWrapper(NodeClient("$apiDomainLocal/Archivist", PayloadTestConstants.TestAccount))

        val payloads = mutableListOf<XyoPayload>()
        payloads.add(PayloadTestConstants.debugPayload)

        runBlocking {
            val insertResult = archivist.insert(payloads, null)
            val (response, errors) = insertResult
            assertNotEquals(response, null)
            assertEquals(errors, null)

            if (response != null) {
                assertTrue(response.contains(PayloadTestConstants.debugPayloadHash))
                assertTrue(response.contains(hash2))
            } else {
                throw(Error("Response should not be null"))
            }
        }

        runBlocking {
            val getResult = archivist.get(arrayListOf(PayloadTestConstants.debugPayloadHash), null)
            assertNotEquals(getResult.response, null)

            val response = getResult.response
            if (response != null) {
                assertTrue(response.contains("network.xyo.debug"))
            } else {
                throw(Error("Response should not be null"))
            }
        }
    }
}