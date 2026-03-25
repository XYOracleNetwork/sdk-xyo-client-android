package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonRpcBlockViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcBlockViewer(transport)

    @Test
    fun `currentBlock returns a block`() = runBlocking {
        val block = viewer.currentBlock()
        assertNotNull(block)
        assertTrue(block.hash.isNotEmpty(), "Block hash should not be empty")
    }

    @Test
    fun `blocksByNumber returns blocks for a given block number`() = runBlocking {
        val blocks = viewer.blocksByNumber(XL1BlockNumber(1), 1)
        assertNotNull(blocks)
        assertTrue(blocks.isNotEmpty(), "Should return at least one block for block number 1")
    }

    @Test
    fun `blockByNumber returns a single block`() = runBlocking {
        val block = viewer.blockByNumber(XL1BlockNumber(1))
        assertNotNull(block, "Should find a block at block number 1")
        assertTrue(block!!.hash.isNotEmpty(), "Block hash should not be empty")
    }

    @Test
    fun `blockByHash returns a block for a known hash`() = runBlocking {
        val block1 = viewer.blockByNumber(XL1BlockNumber(1))
        assertNotNull(block1)
        val block = viewer.blockByHash(block1!!.hash)
        assertNotNull(block, "Should find block by hash")
    }
}
