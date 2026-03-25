package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TransactionViewer

fun rpcMethodHandlersFromTransactionViewer(viewer: TransactionViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.TX_VIEWER_BY_HASH to RpcMethodHandler { params ->
        viewer.byHash(params[0] as String)
    },
    RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX to RpcMethodHandler { params ->
        viewer.byBlockHashAndIndex(params[0] as String, (params[1] as Number).toInt())
    },
    RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX to RpcMethodHandler { params ->
        viewer.byBlockNumberAndIndex((params[0] as Number).toLong(), (params[1] as Number).toInt())
    },
    RpcMethodNames.TX_VIEWER_TX_BY_HASH to RpcMethodHandler { params ->
        viewer.transactionByHash(params[0] as String)
    },
)
