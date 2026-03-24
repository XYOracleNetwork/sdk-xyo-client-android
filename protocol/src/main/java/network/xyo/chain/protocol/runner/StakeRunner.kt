package network.xyo.chain.protocol.runner

import network.xyo.chain.protocol.provider.Provider
import java.math.BigInteger

interface StakeRunner : Provider {
    override val moniker: String get() = MONIKER

    suspend fun addStake(staked: String, amount: BigInteger): Boolean
    suspend fun removeStake(slot: Long): Boolean
    suspend fun withdrawStake(slot: Long): Boolean

    companion object {
        const val MONIKER = "StakeRunner"
    }
}
