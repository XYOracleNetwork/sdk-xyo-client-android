package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TransferBalanceHistoryItem
import network.xyo.chain.protocol.xl1.AttoXL1
import java.math.BigInteger

val TransferBalanceViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<AttoXL1>(RpcMethodNames.TRANSFER_BALANCE_VIEWER_BALANCE) { raw ->
        AttoXL1(BigInteger(raw.toString()))
    }
    method<List<TransferBalanceHistoryItem>>(RpcMethodNames.TRANSFER_BALANCE_VIEWER_HISTORY)
    method<Map<String, Map<String, AttoXL1>>>(RpcMethodNames.TRANSFER_BALANCE_VIEWER_BALANCES)
    method<AttoXL1>(RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_BALANCE) { raw ->
        AttoXL1(BigInteger(raw.toString()))
    }
    method<List<TransferBalanceHistoryItem>>(RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_HISTORY)
    method<Map<String, Map<String, AttoXL1>>>(RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_BALANCES)
}
