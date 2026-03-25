package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.FinalizationViewer

fun rpcMethodHandlersFromFinalizationViewer(viewer: FinalizationViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.FINALIZATION_VIEWER_HEAD to RpcMethodHandler { viewer.head() },
)
