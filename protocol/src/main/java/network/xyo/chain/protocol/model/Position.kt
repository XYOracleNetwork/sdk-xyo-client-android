package network.xyo.chain.protocol.model

import java.math.BigInteger

typealias PositionId = Int

data class Position(
    val addBlock: Long,
    val amount: BigInteger,
    val id: PositionId,
    val removeBlock: Long,
    val staked: String,
    val staker: String,
    val withdrawBlock: Long,
) {
    val isActive: Boolean get() = removeBlock == 0L && withdrawBlock == 0L
    val isRemoved: Boolean get() = removeBlock > 0 && withdrawBlock == 0L
    val isWithdrawn: Boolean get() = withdrawBlock > 0
}
