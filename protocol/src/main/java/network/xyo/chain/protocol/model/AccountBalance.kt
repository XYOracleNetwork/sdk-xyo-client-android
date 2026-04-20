package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.block.BlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.transaction.TransactionBoundWitness

data class AccountBalanceHistoryItem(
    val block: BlockBoundWitnessWithHashMeta,
    val transaction: TransactionBoundWitness?,
    val transfer: TransferWithHashMeta,
)

data class ChainQualification(
    val head: String,
    val range: XL1BlockRange,
)

data class ChainQualified<T>(
    val data: T,
    val qualification: ChainQualification,
)

data class AccountBalanceConfig(
    val range: XL1BlockRange? = null,
    val head: String? = null,
) {
    init {
        require(range == null || head == null) { "AccountBalanceConfig may specify range or head, but not both" }
    }
}
