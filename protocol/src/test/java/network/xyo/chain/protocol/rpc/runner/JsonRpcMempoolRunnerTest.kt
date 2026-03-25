package network.xyo.chain.protocol.rpc.runner

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonRpcMempoolRunnerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val runner = JsonRpcMempoolRunner(transport)

    @Test
    fun `submitTransactions with empty list returns empty list`() = runBlocking {
        val result = runner.submitTransactions(emptyList())
        assertNotNull(result, "Result should not be null")
        assertTrue(result.isEmpty(), "Submitting empty list should return empty list")
    }

    @Test
    fun `submitBlocks with empty list returns empty list`() = runBlocking {
        val result = runner.submitBlocks(emptyList())
        assertNotNull(result, "Result should not be null")
        assertTrue(result.isEmpty(), "Submitting empty list should return empty list")
    }

    @Test
    fun `prunePendingTransactions returns a zero pair`() = runBlocking {
        // Stub implementation (TODO in source)
        val result = runner.prunePendingTransactions()
        assertTrue(result.first == 0 && result.second == 0)
    }

    @Test
    fun `prunePendingBlocks returns a zero pair`() = runBlocking {
        // Stub implementation (TODO in source)
        val result = runner.prunePendingBlocks()
        assertTrue(result.first == 0 && result.second == 0)
    }
}
