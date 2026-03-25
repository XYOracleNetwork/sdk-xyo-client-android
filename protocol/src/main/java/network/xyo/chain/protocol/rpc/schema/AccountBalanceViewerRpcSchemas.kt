package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.xl1.AttoXL1
import java.math.BigInteger

val AccountBalanceViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Map<String, AttoXL1>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_BALANCES) { raw ->
        parseAccountBalances(raw)
    }
    method<List<AccountBalanceHistoryItem>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_HISTORY)
    method<Map<String, List<AccountBalanceHistoryItem>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_HISTORIES)
    method<ChainQualified<Map<String, AttoXL1>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES)
    method<ChainQualified<Map<String, List<AccountBalanceHistoryItem>>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES)
}

@Suppress("UNCHECKED_CAST")
private fun parseAccountBalances(raw: Any?): Map<String, AttoXL1> {
    val map = raw as? Map<String, Any> ?: return emptyMap()
    return map.mapValues { (_, v) ->
        when (v) {
            is String -> AttoXL1.fromHex(v)
            is Number -> AttoXL1(BigInteger.valueOf(v.toLong()))
            else -> AttoXL1.ZERO
        }
    }
}
