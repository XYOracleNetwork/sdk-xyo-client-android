package network.xyo.chain.protocol.rpc.live

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.viewer.JsonRpcFinalizationViewer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "XL1_RPC_URL", matches = ".+")
class LiveFinalizationViewerTest {
    private val viewer = JsonRpcFinalizationViewer(LiveRpcSupport.transport())

    @Test
    fun `head returns the finalized block (head minus 6 from stub)`() = runBlocking {
        val block = viewer.head()
        assertEquals(
            LiveRpcSupport.expectedBlockHash(LiveRpcSupport.FINALIZED_BLOCK_NUMBER),
            block.hash,
        )
        assertEquals(
            LiveRpcSupport.FINALIZED_BLOCK_NUMBER,
            block.boundWitness.block,
        )
    }
}
