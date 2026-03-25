package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.AccountBalanceViewer

fun rpcMethodHandlersFromAccountBalanceViewer(viewer: AccountBalanceViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val addresses = params[0] as List<String>
        val config = params[1] as AccountBalanceConfig
        viewer.qualifiedAccountBalances(addresses, config)
    },
    RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val addresses = params[0] as List<String>
        val config = params[1] as AccountBalanceConfig
        viewer.qualifiedAccountBalanceHistories(addresses, config)
    },
)
