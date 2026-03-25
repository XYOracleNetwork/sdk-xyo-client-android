package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.TransactionViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.viewer.TransactionViewer

class JsonRpcTransactionViewer(
    private val transport: RpcTransport,
) : TransactionViewer {
    override val moniker: String = TransactionViewer.MONIKER

    private val schemas = TransactionViewerRpcSchemas

    override suspend fun byHash(transactionHash: String): SignedHydratedTransactionWithHashMeta? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_HASH, listOf(transactionHash), schemas)
    }

    override suspend fun byBlockHashAndIndex(blockHash: String, transactionIndex: Int): SignedHydratedTransactionWithHashMeta? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX, listOf(blockHash, transactionIndex), schemas)
    }

    override suspend fun byBlockNumberAndIndex(blockNumber: Long, transactionIndex: Int): SignedHydratedTransactionWithHashMeta? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX, listOf(blockNumber, transactionIndex), schemas)
    }

    override suspend fun transactionByHash(transactionHash: String): SignedHydratedTransaction? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_TX_BY_HASH, listOf(transactionHash), schemas)
    }

    override suspend fun transactionByBlockHashAndIndex(blockHash: String, transactionIndex: Int): SignedHydratedTransaction? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX, listOf(blockHash, transactionIndex), schemas)
    }

    override suspend fun transactionByBlockNumberAndIndex(blockNumber: Long, transactionIndex: Int): SignedHydratedTransaction? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX, listOf(blockNumber, transactionIndex), schemas)
    }
}
