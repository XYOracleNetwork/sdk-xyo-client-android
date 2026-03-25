package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.NetworkStakeViewer

fun rpcMethodHandlersFromNetworkStakeViewer(viewer: NetworkStakeViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.NETWORK_STAKE_VIEWER_ACTIVE to RpcMethodHandler { params ->
        val blockNumber = (params.getOrNull(0) as? Number)?.toLong()?.let { XL1BlockNumber(it) }
        val result = viewer.active(blockNumber)
        listOf(result.totalStake.toString(), result.validatorCount)
    },
)
