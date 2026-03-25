package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.schema.MempoolViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.viewer.MempoolViewer
import network.xyo.chain.protocol.viewer.PendingBlocksOptions
import network.xyo.chain.protocol.viewer.PendingTransactionsOptions

class JsonRpcMempoolViewer(
    private val transport: RpcTransport,
) : MempoolViewer {
    override val moniker: String = MempoolViewer.MONIKER

    private val schemas = MempoolViewerRpcSchemas

    override suspend fun pendingTransactions(options: PendingTransactionsOptions?): List<SignedHydratedTransactionWithHashMeta> {
        val params = if (options != null) listOf(options) else emptyList()
        return transport.sendRequest(RpcMethodNames.MEMPOOL_VIEWER_PENDING_TRANSACTIONS, params, schemas)
    }

    override suspend fun pendingBlocks(options: PendingBlocksOptions?): List<SignedHydratedBlockWithHashMeta> {
        val params = if (options != null) listOf(options) else emptyList()
        return transport.sendRequest(RpcMethodNames.MEMPOOL_VIEWER_PENDING_BLOCKS, params, schemas)
    }
}
