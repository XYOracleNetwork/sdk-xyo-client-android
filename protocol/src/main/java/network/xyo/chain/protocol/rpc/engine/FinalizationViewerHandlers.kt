package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.FinalizationViewer

fun rpcMethodHandlersFromFinalizationViewer(viewer: FinalizationViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.FINALIZATION_VIEWER_HEAD to RpcMethodHandler { viewer.head() },
    RpcMethodNames.FINALIZATION_VIEWER_HEAD_BLOCK to RpcMethodHandler { viewer.headBlock() },
    RpcMethodNames.FINALIZATION_VIEWER_HEAD_HASH to RpcMethodHandler { viewer.headHash() },
    RpcMethodNames.FINALIZATION_VIEWER_HEAD_NUMBER to RpcMethodHandler { viewer.headNumber() },
    RpcMethodNames.FINALIZATION_VIEWER_CHAIN_ID to RpcMethodHandler { viewer.chainId() },
)
