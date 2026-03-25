package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.xl1.AttoXL1

val AccountBalanceViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<ChainQualified<Map<String, AttoXL1>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES)
    method<ChainQualified<Map<String, List<AccountBalanceHistoryItem>>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES)
}
