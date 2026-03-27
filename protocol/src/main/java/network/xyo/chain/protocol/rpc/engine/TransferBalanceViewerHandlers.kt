package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TransferBalanceViewer

fun rpcMethodHandlersFromTransferBalanceViewer(viewer: TransferBalanceViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.TRANSFER_BALANCE_VIEWER_BALANCE to RpcMethodHandler { params ->
        viewer.transferBalance(params[0] as String)
    },
    RpcMethodNames.TRANSFER_BALANCE_VIEWER_HISTORY to RpcMethodHandler { params ->
        viewer.transferBalanceHistory(params[0] as String)
    },
    RpcMethodNames.TRANSFER_BALANCE_VIEWER_BALANCES to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        viewer.transferBalances(params[0] as List<String>)
    },
    RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_BALANCE to RpcMethodHandler { params ->
        viewer.transferPairBalance(Pair(params[0] as String, params[1] as String))
    },
    RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_HISTORY to RpcMethodHandler { params ->
        viewer.transferPairBalanceHistory(Pair(params[0] as String, params[1] as String))
    },
    RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_BALANCES to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        viewer.transferPairBalances(params[0] as List<Pair<String, String>>)
    },
)
