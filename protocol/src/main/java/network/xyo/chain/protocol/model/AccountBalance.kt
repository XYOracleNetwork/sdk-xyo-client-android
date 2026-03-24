package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.block.BlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.transaction.TransactionBoundWitness

data class AccountBalanceHistoryItem(
    val block: BlockBoundWitnessWithHashMeta,
    val transaction: TransactionBoundWitness?,
    val transfer: TransferWithHashMeta,
)

typealias ChainQualified<T> = Map<String, T>

data class AccountBalanceConfig(
    val qualifications: List<String>? = null,
)
