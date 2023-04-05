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
        val client = NodeClient(apiDomainLocal)
        val payload = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val address = XyoAccount()
            val postResult = client.query(payload, address, null)
            assertEquals(null, postResult.errors)
        }
    }

    @Test
    fun AddressTest() {
        val client = NodeClient(apiDomainLocal)
        val payload = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val address = XyoAccount()
            val postResult = client.query(payload, address, null)
            assertNotEquals(postResult.response, null)
        }
    }
}