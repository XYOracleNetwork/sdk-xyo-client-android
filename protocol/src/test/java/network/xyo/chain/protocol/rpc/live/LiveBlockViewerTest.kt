package network.xyo.chain.protocol.rpc.live

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.viewer.JsonRpcBlockViewer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "XL1_RPC_URL", matches = ".+")
class LiveBlockViewerTest {
    private val viewer = JsonRpcBlockViewer(LiveRpcSupport.transport())

    @Test
    fun `currentBlock returns the stub head`() = runBlocking {
        val block = viewer.currentBlock()
        assertNotNull(block)
        assertEquals(LiveRpcSupport.expectedBlockHash(LiveRpcSupport.HEAD_BLOCK_NUMBER), block.hash)
        assertEquals(LiveRpcSupport.HEAD_BLOCK_NUMBER, block.boundWitness.block)
        assertEquals(LiveRpcSupport.CHAIN_ID, block.boundWitness.chain)
        assertEquals(listOf(LiveRpcSupport.STUB_ADDRESS), block.boundWitness.addresses)
    }

    @Test
    fun `blocksByNumber returns descending blocks capped by limit`() = runBlocking {
        val blocks = viewer.blocksByNumber(XL1BlockNumber(100), 5)
        assertEquals(5, blocks.size)
        val numbers = blocks.map { it.boundWitness.block }
        assertEquals(listOf(100L, 99L, 98L, 97L, 96L), numbers)
    }

    @Test
    fun `blocksByHash round-trips against a freshly-fetched hash`() = runBlocking {
        val head = viewer.currentBlock()
        val blocks = viewer.blocksByHash(head.hash, 3)
        assertEquals(3, blocks.size)
        assertEquals(head.hash, blocks.first().hash)
    }

    @Test
    fun `blockByNumber returns single block`() = runBlocking {
        val block = viewer.blockByNumber(XL1BlockNumber(42))
        assertNotNull(block)
        assertEquals(LiveRpcSupport.expectedBlockHash(42), block!!.hash)
    }
}
