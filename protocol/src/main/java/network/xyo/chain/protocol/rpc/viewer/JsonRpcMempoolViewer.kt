package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.viewer.MempoolViewer
import network.xyo.chain.protocol.viewer.PendingBlocksOptions
import network.xyo.chain.protocol.viewer.PendingTransactionsOptions

class JsonRpcMempoolViewer(
    private val transport: RpcTransport,
) : MempoolViewer {
    override val moniker: String = MempoolViewer.MONIKER

    override suspend fun pendingTransactions(options: PendingTransactionsOptions?): List<SignedHydratedTransactionWithHashMeta> {
        val params = if (options != null) listOf(options) else emptyList()
        transport.sendRequest(RpcMethodNames.MEMPOOL_VIEWER_PENDING_TRANSACTIONS, params)
        // TODO: deserialize
        return emptyList()
    }

    override suspend fun pendingBlocks(options: PendingBlocksOptions?): List<SignedHydratedBlockWithHashMeta> {
        val params = if (options != null) listOf(options) else emptyList()
        transport.sendRequest(RpcMethodNames.MEMPOOL_VIEWER_PENDING_BLOCKS, params)
        // TODO: deserialize
        return emptyList()
    }
}
