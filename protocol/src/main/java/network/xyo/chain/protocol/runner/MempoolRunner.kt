package network.xyo.chain.protocol.runner

import network.xyo.chain.protocol.block.SignedHydratedBlock
import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction

data class MempoolPruneOptions(
    val batchSize: Int? = null,
    val maxCheck: Int? = null,
    val maxPrune: Int? = null,
)

interface MempoolRunner : Provider {
    override val moniker: String get() = MONIKER

    suspend fun submitTransactions(transactions: List<SignedHydratedTransaction>): List<String>
    suspend fun submitBlocks(blocks: List<SignedHydratedBlock>): List<String>
    suspend fun prunePendingTransactions(options: MempoolPruneOptions? = null): Pair<Int, Int>
    suspend fun prunePendingBlocks(options: MempoolPruneOptions? = null): Pair<Int, Int>

    companion object {
        const val MONIKER = "MempoolRunner"
        const val DEFAULT_MAX_EXP_AHEAD = 1000
    }
}
