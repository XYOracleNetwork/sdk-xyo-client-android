package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.provider.Provider
import java.math.BigInteger

interface NetworkStakeViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun totalStake(): BigInteger
    suspend fun positionCount(): Int

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
