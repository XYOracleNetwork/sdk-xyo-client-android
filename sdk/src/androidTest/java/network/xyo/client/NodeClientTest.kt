package network.xyo.client

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.Account
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class NodeClientTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun discoverTest() {
        val account = Account()
        val client = NodeClient(TestConstants.nodeUrlLocal, account)
        val query = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val postResult = client.query(query, null, null)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun archivistInsertTest() {
        val hash2 = "2967d5719b610e1dfa38651e8ff1a67d47b7bb4cea4ccfc8735b05d1660ef36f"
        val archivist = ArchivistWrapper(NodeClient("${TestConstants.nodeUrlLocal}/Archivist", TestConstants.TestAccount))

        val payloads = arrayListOf(TestConstants.debugPayload)

        runBlocking {
            val (response, errors) = archivist.insert(payloads, null)
            assertNotEquals(response, null)
            assertEquals(errors, null)

            if (response != null) {
                assertEquals(response?.payloads?.get(0)?.schema, "network.xyo.boundwitness")
            } else {
                throw(Error("Response should not be null"))
            }
        }

        runBlocking {
            val getResult = archivist.get(arrayListOf(TestConstants.debugPayloadHash), null)
            assertNotEquals(getResult.response, null)

            val response = getResult.response
            if (response != null) {
                assertEquals(response?.payloads?.get(0)?.schema, TestConstants.debugPayload.schema)
            } else {
                throw(Error("Response should not be null"))
            }
        }
    }
}