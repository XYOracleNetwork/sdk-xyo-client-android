package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class JsonRpcTransactionViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcTransactionViewer(transport)

    // A 64-char hex hash that won't match any real transaction
    private val nonExistentHash = "0000000000000000000000000000000000000000000000000000000000000000"

    @Test
    fun `byHash returns null for non-existent transaction`() = runBlocking {
        val tx = viewer.byHash(nonExistentHash)
        assertNull(tx, "Should return null for a non-existent transaction hash")
    }
}
