package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.NetworkStakeViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.NetworkStakeViewer
import java.math.BigInteger

class JsonRpcNetworkStakeViewer(
    private val transport: RpcTransport,
) : NetworkStakeViewer {
    override val moniker: String = NetworkStakeViewer.MONIKER

    private val schemas = NetworkStakeViewerRpcSchemas

    override suspend fun totalStake(): BigInteger {
        return transport.sendRequest(RpcMethodNames.NETWORK_STAKE_VIEWER_TOTAL_STAKE, schemas = schemas)
    }

    override suspend fun positionCount(): Int {
        return transport.sendRequest(RpcMethodNames.NETWORK_STAKE_VIEWER_POSITION_COUNT, schemas = schemas)
    }
}
