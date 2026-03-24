package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta

data class PendingTransactionsOptions(
    val cursor: String? = null,
    val limit: Int? = null,
    val window: XL1BlockRange? = null,
)

data class PendingBlocksOptions(
    val cursor: String? = null,
    val limit: Int? = null,
    val window: XL1BlockRange? = null,
)

interface MempoolViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun pendingTransactions(options: PendingTransactionsOptions? = null): List<SignedHydratedTransactionWithHashMeta>
    suspend fun pendingBlocks(options: PendingBlocksOptions? = null): List<SignedHydratedBlockWithHashMeta>

    companion object {
        const val MONIKER = "MempoolViewer"
    }
}
