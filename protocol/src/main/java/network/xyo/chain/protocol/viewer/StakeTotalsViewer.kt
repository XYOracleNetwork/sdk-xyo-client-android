package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider
import java.math.BigInteger

interface StakeTotalsViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun active(time: Long? = null): BigInteger
    suspend fun activeByStaked(staked: String, time: Long? = null): BigInteger
    suspend fun activeByStaker(address: String, time: Long? = null): BigInteger
    suspend fun pending(time: Long? = null): BigInteger
    suspend fun pendingByStaker(staker: String, time: Long? = null): BigInteger
    suspend fun withdrawn(time: Long? = null): BigInteger
    suspend fun withdrawnByStaker(staker: String, time: Long? = null): BigInteger

    companion object {
        const val MONIKER = "StakeTotalsViewer"
    }
}
