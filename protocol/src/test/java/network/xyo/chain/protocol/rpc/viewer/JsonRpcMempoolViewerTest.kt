package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class JsonRpcMempoolViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcMempoolViewer(transport)

    @Test
    fun `pendingTransactions returns a list`() = runBlocking {
        val transactions = viewer.pendingTransactions()
        assertNotNull(transactions, "Pending transactions should not be null")
    }

    @Test
    fun `pendingBlocks returns a list`() = runBlocking {
        val blocks = viewer.pendingBlocks()
        assertNotNull(blocks, "Pending blocks should not be null")
    }
}
