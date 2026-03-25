package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import network.xyo.chain.protocol.viewer.TimeDomain
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonRpcTimeSyncViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcTimeSyncViewer(transport)

    @Test
    fun `currentTimePayload returns a time payload`() = runBlocking {
        val payload = viewer.currentTimePayload()
        assertNotNull(payload)
        assertTrue(payload.isNotEmpty(), "Time payload should not be empty")
    }

    @Test
    fun `currentTime returns domain and value`() = runBlocking {
        val result = viewer.currentTime(TimeDomain.epoch)
        assertNotNull(result)
        assertTrue(result.second > 0, "Time value should be positive")
    }

    @Test
    fun `currentTimeAndHash returns time and hash`() = runBlocking {
        val result = viewer.currentTimeAndHash(TimeDomain.xl1)
        assertNotNull(result)
        assertTrue(result.first > 0, "Time value should be positive")
    }

    @Test
    fun `convertTime converts xl1 to epoch`() = runBlocking {
        val result = viewer.convertTime(TimeDomain.xl1, TimeDomain.epoch, 100000)
        assertTrue(result > 0, "Converted time should be positive")
    }
}
