package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import network.xyo.chain.protocol.rpc.transport.RpcTransportException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonRpcNetworkStakeViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcNetworkStakeViewer(transport)

    @Test
    fun `active is not available on beta`() {
        // networkStakeViewer_active is defined in the schema but not deployed on beta
        assertThrows<RpcTransportException> {
            runBlocking { viewer.active() }
        }
    }
}
