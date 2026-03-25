package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.provider.Provider
import java.math.BigInteger

/**
 * Result of the networkStakeViewer_active RPC call.
 * Contains the total active stake and the number of active validators.
 */
data class ActiveStakeResult(
    val totalStake: BigInteger,
    val validatorCount: Int,
)

interface NetworkStakeViewer : Provider {
    override val moniker: String get() = MONIKER

    /** Get the active stake total and validator count, optionally at a specific block. */
    suspend fun active(blockNumber: XL1BlockNumber? = null): ActiveStakeResult

    companion object {
        const val MONIKER = "NetworkStakeViewer"
    }
}

data class PagedPositionsOptions(
    val cursor: PositionId? = null,
    val limit: Int? = null,
)

data class PagedStakersOptions(
    val cursor: String? = null,
    val limit: Int? = null,
)

interface StepViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun positionCount(step: Int): Int
    suspend fun positions(step: Int, options: PagedPositionsOptions? = null): List<Position>
    suspend fun randomizer(step: Int): BigInteger
    suspend fun stake(step: Int): BigInteger
    suspend fun stakerCount(step: Int): Int
    suspend fun stakers(step: Int, options: PagedStakersOptions? = null): List<String>
    suspend fun weight(step: Int, positionId: PositionId? = null): BigInteger

    companion object {
        const val MONIKER = "StepViewer"
    }
}

interface NetworkStakeStepRewardsViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun byPosition(positionId: PositionId): BigInteger
    suspend fun byStaker(staker: String): BigInteger
    suspend fun byStep(step: Int): BigInteger
    suspend fun total(): BigInteger

    companion object {
        const val MONIKER = "NetworkStakeStepRewardsViewer"
    }
}
