package network.xyo.chain.protocol.rpc.live

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.viewer.JsonRpcMempoolViewer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "XL1_RPC_URL", matches = ".+")
class LiveMempoolViewerTest {
    private val viewer = JsonRpcMempoolViewer(LiveRpcSupport.transport())

    @Test
    fun `pendingTransactions is empty on a fresh stub`() = runBlocking {
        val txs = viewer.pendingTransactions(null)
        assertTrue(txs.isEmpty(), "expected empty pending-tx list, got $txs")
    }

    @Test
    fun `pendingBlocks is empty on a fresh stub`() = runBlocking {
        val blocks = viewer.pendingBlocks(null)
        assertTrue(blocks.isEmpty(), "expected empty pending-blocks list, got $blocks")
    }
}
