package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta

val TransactionViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<SignedHydratedTransactionWithHashMeta>(RpcMethodNames.TX_VIEWER_BY_HASH)
    method<SignedHydratedTransactionWithHashMeta>(RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX)
    method<SignedHydratedTransactionWithHashMeta>(RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX)
    method<SignedHydratedTransaction>(RpcMethodNames.TX_VIEWER_TX_BY_HASH)
}
