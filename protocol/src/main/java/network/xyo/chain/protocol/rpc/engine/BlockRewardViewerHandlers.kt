package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.BlockRewardViewer

fun rpcMethodHandlersFromBlockRewardViewer(viewer: BlockRewardViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.BLOCK_REWARD_VIEWER_ALLOWED_REWARD to RpcMethodHandler { params ->
        viewer.allowedRewardForBlock(XL1BlockNumber((params[0] as Number).toLong()))
    },
)
