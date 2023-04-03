package network.xyo.client

import kotlinx.coroutines.runBlocking
import network.xyo.client.node.client.NodeClient
import network.xyo.client.payload.XyoPayload
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class NodeClientTest {

    @Test
    fun testUrl() {
        val url = "http://localhost:8080/RootStorageArchivist"
        val client = NodeClient(url)
        assertEquals(client.url, url)
        val payload = XyoPayload("network.xyo.discover.query")

        runBlocking {
            client.callAsync(payload)
        }
    }
}