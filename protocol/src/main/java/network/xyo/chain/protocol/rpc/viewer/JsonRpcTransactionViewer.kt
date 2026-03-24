package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.viewer.TransactionViewer

class JsonRpcTransactionViewer(
    private val transport: RpcTransport,
) : TransactionViewer {
    override val moniker: String = TransactionViewer.MONIKER

    override suspend fun byHash(transactionHash: String): SignedHydratedTransactionWithHashMeta? {
        transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_HASH, listOf(transactionHash))
        // TODO: deserialize response
        return null
    }

    override suspend fun byBlockHashAndIndex(blockHash: String, transactionIndex: Int): SignedHydratedTransactionWithHashMeta? {
        transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX, listOf(blockHash, transactionIndex))
        // TODO: deserialize response
        return null
    }

    override suspend fun byBlockNumberAndIndex(blockNumber: Long, transactionIndex: Int): SignedHydratedTransactionWithHashMeta? {
        transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX, listOf(blockNumber, transactionIndex))
        // TODO: deserialize response
        return null
    }

    override suspend fun transactionByHash(transactionHash: String): SignedHydratedTransaction? {
        transport.sendRequest(RpcMethodNames.TX_VIEWER_TX_BY_HASH, listOf(transactionHash))
        // TODO: deserialize response
        return null
    }

    override suspend fun transactionByBlockHashAndIndex(blockHash: String, transactionIndex: Int): SignedHydratedTransaction? {
        transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX, listOf(blockHash, transactionIndex))
        // TODO: deserialize response
        return null
    }

    override suspend fun transactionByBlockNumberAndIndex(blockNumber: Long, transactionIndex: Int): SignedHydratedTransaction? {
        transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX, listOf(blockNumber, transactionIndex))
        // TODO: deserialize response
        return null
    }
}
