package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.provider.Provider

interface StakeViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun stakeById(id: PositionId): Position
    suspend fun stakeByStaker(staker: String, slot: Int): Position
    suspend fun stakesByStaked(staked: String): List<Position>
    suspend fun stakesByStaker(staker: String): List<Position>
    suspend fun minWithdrawalBlocks(): Long
    suspend fun rewardsContract(): String
    suspend fun stakingTokenAddress(): String

    companion object {
        const val MONIKER = "StakeViewer"
    }
}
