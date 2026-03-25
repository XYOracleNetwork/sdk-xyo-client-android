package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta

interface TransactionViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun byHash(transactionHash: String): SignedHydratedTransactionWithHashMeta?

    companion object {
        const val MONIKER = "TransactionViewer"
    }
}
