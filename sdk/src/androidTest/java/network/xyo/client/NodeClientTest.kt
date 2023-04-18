package network.xyo.client

import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class GetPayload(val hashes: MutableList<String>): XyoPayload("network.xyo.query.archivist.get") {}
class DebugPayload(schema: String, val nonce: Int) : XyoPayload(schema) {}
data class ArchivistInsertPayload(val payloads: List<String>): XyoPayload("network.xyo.query.archivist.insert")

class NodeClientTest {

    val apiDomainLocal = "http://10.0.2.2:8080"

    @Test
    fun DiscoverTest() {
        val account = XyoAccount()
        val client = NodeClient(apiDomainLocal, account)
        val query = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val postResult = client.query(query, null, null)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun ArchivistInsertTest() {
        val hash1 = "15b8d0e30ca5aa96ca6cc9e1528c075aec88cd3f2c3eb0394fde647eb4bf4547"
        val hash2 = "2967d5719b610e1dfa38651e8ff1a67d47b7bb4cea4ccfc8735b05d1660ef36f"

        val account = XyoAccount(XyoSerializable.hexToBytes("69f0b123c094c34191f22c25426036d6e46d5e1fab0a04a164b3c1c2621152ab"))
        val client = NodeClient("$apiDomainLocal/Archivist", account)

        val debugPayload = DebugPayload("network.xyo.debug",1)
        val query = ArchivistInsertPayload(arrayListOf(XyoSerializable.sha256String(debugPayload)))

        val payloads = mutableListOf<XyoPayload>()
        payloads.add(debugPayload)

        runBlocking {
            val postResult = client.query(query, payloads, null)
            assertNotEquals(postResult.response, null)

            val response = postResult.response
            if (response != null) {
                assertTrue(response.contains(hash1))
                assertTrue(response.contains(hash2))
            } else {
                throw(Error("Response should not be null"))
            }
        }

        val hashes = mutableListOf<String>()
        hashes.add(hash1)
        val getQueryPayload = GetPayload(hashes)


        runBlocking {
            val postResult = client.query(getQueryPayload, null, null)
            assertNotEquals(postResult.response, null)

            val response = postResult.response
            if (response != null) {
                assertTrue(response.contains("network.xyo.debug"))
            } else {
                throw(Error("Response should not be null"))
            }
        }
    }
}