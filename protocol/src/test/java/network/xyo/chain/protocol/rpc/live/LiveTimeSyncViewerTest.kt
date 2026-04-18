package network.xyo.chain.protocol.rpc.live

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.viewer.JsonRpcTimeSyncViewer
import network.xyo.chain.protocol.viewer.TimeDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "XL1_RPC_URL", matches = ".+")
class LiveTimeSyncViewerTest {
    private val viewer = JsonRpcTimeSyncViewer(LiveRpcSupport.transport())

    @Test
    fun `currentTime(epoch) returns the stub head epoch`() = runBlocking {
        val (domain, value) = viewer.currentTime(TimeDomain.epoch)
        assertEquals(TimeDomain.epoch, domain)
        assertEquals(LiveRpcSupport.HEAD_EPOCH_MS, value)
    }

    @Test
    fun `currentTime(xl1) returns the stub head block number`() = runBlocking {
        val (domain, value) = viewer.currentTime(TimeDomain.xl1)
        assertEquals(TimeDomain.xl1, domain)
        assertEquals(LiveRpcSupport.HEAD_BLOCK_NUMBER, value)
    }

    @Test
    fun `convertTime round-trip xl1 epoch xl1 preserves value`() = runBlocking {
        val asEpoch = viewer.convertTime(TimeDomain.xl1, TimeDomain.epoch, 123L)
        val backToXl1 = viewer.convertTime(TimeDomain.epoch, TimeDomain.xl1, asEpoch)
        assertEquals(123L, backToXl1)
    }

    @Test
    fun `currentTimeAndHash returns a hex hash alongside the value`() = runBlocking {
        val (value, hash) = viewer.currentTimeAndHash(TimeDomain.xl1)
        assertEquals(LiveRpcSupport.HEAD_BLOCK_NUMBER, value)
        assertNotNull(hash)
        assertEquals(64, hash!!.length)
    }
}
