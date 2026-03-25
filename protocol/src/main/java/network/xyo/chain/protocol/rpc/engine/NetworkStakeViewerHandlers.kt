package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.NetworkStakeViewer

fun rpcMethodHandlersFromNetworkStakeViewer(viewer: NetworkStakeViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.NETWORK_STAKE_VIEWER_TOTAL_STAKE to RpcMethodHandler { viewer.totalStake() },
    RpcMethodNames.NETWORK_STAKE_VIEWER_POSITION_COUNT to RpcMethodHandler { viewer.positionCount() },
)
