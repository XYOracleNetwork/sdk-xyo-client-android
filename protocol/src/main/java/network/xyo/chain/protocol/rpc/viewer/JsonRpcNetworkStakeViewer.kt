package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.NetworkStakeViewer
import java.math.BigInteger

class JsonRpcNetworkStakeViewer(
    private val transport: RpcTransport,
) : NetworkStakeViewer {
    override val moniker: String = NetworkStakeViewer.MONIKER

    override suspend fun totalStake(): BigInteger {
        val result = transport.sendRequest(RpcMethodNames.NETWORK_STAKE_VIEWER_TOTAL_STAKE)
        return when (result) {
            is String -> BigInteger(result.removePrefix("0x"), 16)
            is Number -> BigInteger.valueOf(result.toLong())
            else -> BigInteger.ZERO
        }
    }

    override suspend fun positionCount(): Int {
        val result = transport.sendRequest(RpcMethodNames.NETWORK_STAKE_VIEWER_POSITION_COUNT)
        return (result as Number).toInt()
    }
}
