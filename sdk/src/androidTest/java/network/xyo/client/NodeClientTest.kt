package network.xyo.client

import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class GetPayload(val hashes: MutableList<String>): XyoPayload("network.xyo.query.archivist.get") {}

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
        val hash1 = "7ad2100faa86dd870898e61707786bfac3181bd1ce36ac31e227371060f8456c"
        val hash2 = "3da33603417622f4cdad2becbca8c7889623d9045d0e8923e1702a99d2f3e47c"

        val account = XyoAccount()
        val client = NodeClient("$apiDomainLocal/Archivist", account)

        val query = XyoPayload("network.xyo.query.archivist.insert")
        val payloads = mutableListOf<XyoPayload>()
        payloads.add(XyoPayload("network.xyo.payload"))

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
        hashes.add(hash2)
        val getQueryPayload = GetPayload(hashes)


        runBlocking {
            val postResult = client.query(getQueryPayload, null, null)
            assertNotEquals(postResult.response, null)

            val response = postResult.response
            if (response != null) {
                assertTrue(response.contains("network.xyo.payload"))
            } else {
                throw(Error("Response should not be null"))
            }
        }
    }
}