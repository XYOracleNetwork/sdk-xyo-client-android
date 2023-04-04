package network.xyo.client

import kotlinx.coroutines.runBlocking
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class NodeClientTest {

    val apiDomainLocal = "http://10.0.2.2:8080"

    @Test
    fun testUrl() {
        val client = NodeClient(apiDomainLocal)
        val payload = XyoPayload("network.xyo.query.module.discover")

        runBlocking {
            val postResult = client.callAsync(payload)
            assertEquals(null, postResult.errors)
        }
    }
}