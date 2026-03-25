package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonRpcFinalizationViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcFinalizationViewer(transport)

    @Test
    fun `head returns a block`() = runBlocking {
        val head = viewer.head()
        assertNotNull(head)
        assertTrue(head.hash.isNotEmpty(), "Head hash should not be empty")
    }
}
