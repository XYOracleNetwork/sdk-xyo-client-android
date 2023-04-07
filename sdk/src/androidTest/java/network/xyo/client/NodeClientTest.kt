package network.xyo.client

import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class NodeClientTest {

    val apiDomainLocal = "http://10.0.2.2:8080"

    @Test
    fun DiscoverTest() {
        val account = XyoAccount()
        val client = NodeClient(apiDomainLocal, account)
        val payload = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val postResult = client.query(payload, null)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun AddressTest() {
        val account = XyoAccount()
        val client = NodeClient("$apiDomainLocal/Archivist", account)
        val payload = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val address = XyoAccount()
            val postResult = client.query(payload, null)
            assertNotEquals(postResult.response, null)
        }
    }
}