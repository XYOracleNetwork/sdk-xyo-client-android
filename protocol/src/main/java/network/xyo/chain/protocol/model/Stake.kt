package network.xyo.chain.protocol.model

import com.squareup.moshi.JsonClass
import java.math.BigInteger

@JsonClass(generateAdapter = true)
data class StakeJson(
    val amount: String,
    val addBlock: Long,
    val id: Int,
    val removeBlock: Long,
    val staked: String,
    val staker: String,
    val withdrawBlock: Long,
) {
    fun toStake(): Stake = Stake(
        amount = BigInteger(amount.removePrefix("0x"), 16),
        addBlock = addBlock,
        id = id,
        removeBlock = removeBlock,
        staked = staked,
        staker = staker,
        withdrawBlock = withdrawBlock,
    )
}

data class Stake(
    val amount: BigInteger,
    val addBlock: Long,
    val id: Int,
    val removeBlock: Long,
    val staked: String,
    val staker: String,
    val withdrawBlock: Long,
) {
    fun toJson(): StakeJson = StakeJson(
        amount = "0x${amount.toString(16)}",
        addBlock = addBlock,
        id = id,
        removeBlock = removeBlock,
        staked = staked,
        staker = staker,
        withdrawBlock = withdrawBlock,
    )

    fun toPosition(): Position = Position(
        addBlock = addBlock,
        amount = amount,
        id = id,
        removeBlock = removeBlock,
        staked = staked,
        staker = staker,
        withdrawBlock = withdrawBlock,
    )
}
