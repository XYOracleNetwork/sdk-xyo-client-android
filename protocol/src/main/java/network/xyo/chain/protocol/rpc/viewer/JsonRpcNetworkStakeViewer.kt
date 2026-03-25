package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.schema.NetworkStakeViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.ActiveStakeResult
import network.xyo.chain.protocol.viewer.NetworkStakeViewer

class JsonRpcNetworkStakeViewer(
    private val transport: RpcTransport,
) : NetworkStakeViewer {
    override val moniker: String = NetworkStakeViewer.MONIKER

    private val schemas = NetworkStakeViewerRpcSchemas

    override suspend fun active(blockNumber: XL1BlockNumber?): ActiveStakeResult {
        val params = if (blockNumber != null) listOf(blockNumber.value) else emptyList()
        return transport.sendRequest(RpcMethodNames.NETWORK_STAKE_VIEWER_ACTIVE, params, schemas)
    }
}
