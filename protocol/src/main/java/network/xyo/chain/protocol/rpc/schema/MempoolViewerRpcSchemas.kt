package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta

val MempoolViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<SignedHydratedTransactionWithHashMeta>>(RpcMethodNames.MEMPOOL_VIEWER_PENDING_TRANSACTIONS)
    method<List<SignedHydratedBlockWithHashMeta>>(RpcMethodNames.MEMPOOL_VIEWER_PENDING_BLOCKS)
}
