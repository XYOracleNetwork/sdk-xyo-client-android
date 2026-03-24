package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta

interface TransactionViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun byHash(transactionHash: String): SignedHydratedTransactionWithHashMeta?
    suspend fun byBlockHashAndIndex(blockHash: String, transactionIndex: Int): SignedHydratedTransactionWithHashMeta?
    suspend fun byBlockNumberAndIndex(blockNumber: Long, transactionIndex: Int): SignedHydratedTransactionWithHashMeta?
    suspend fun transactionByHash(transactionHash: String): SignedHydratedTransaction?
    suspend fun transactionByBlockHashAndIndex(blockHash: String, transactionIndex: Int): SignedHydratedTransaction?
    suspend fun transactionByBlockNumberAndIndex(blockNumber: Long, transactionIndex: Int): SignedHydratedTransaction?

    companion object {
        const val MONIKER = "TransactionViewer"
    }
}
