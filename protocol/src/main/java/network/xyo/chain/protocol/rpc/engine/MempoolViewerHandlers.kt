package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.MempoolViewer

fun rpcMethodHandlersFromMempoolViewer(viewer: MempoolViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.MEMPOOL_VIEWER_PENDING_TRANSACTIONS to RpcMethodHandler { viewer.pendingTransactions() },
    RpcMethodNames.MEMPOOL_VIEWER_PENDING_BLOCKS to RpcMethodHandler { viewer.pendingBlocks() },
)
