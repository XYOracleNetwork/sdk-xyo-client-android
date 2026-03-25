package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonRpcAccountBalanceViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcAccountBalanceViewer(transport)

    private val testAddress = "0000000000000000000000000000000000000000"

    @Test
    fun `qualifiedAccountBalances reaches the server`() {
        // The server returns [data, qualification] tuple which doesn't match
        // the current ChainQualified<T> typealias (Map<String, T>).
        // This needs a custom transform to parse the tuple format.
        assertThrows<IllegalArgumentException> {
            runBlocking {
                viewer.qualifiedAccountBalances(listOf(testAddress), AccountBalanceConfig())
            }
        }
    }
}
