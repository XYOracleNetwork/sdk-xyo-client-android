package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TransactionViewer

fun rpcMethodHandlersFromTransactionViewer(viewer: TransactionViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.TX_VIEWER_BY_HASH to RpcMethodHandler { params ->
        viewer.byHash(params[0] as String)
    },
)
