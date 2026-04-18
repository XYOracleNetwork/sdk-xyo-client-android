package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider
import java.math.BigInteger

/**
 * Per-dimension step-rewards viewer options, mirroring the JS
 * `NetworkStakeStepRewardsByXxxViewerOptions`. All fields are optional — when
 * none are set the viewer returns aggregates across the full domain.
 *
 * `range` is a `[fromBlock, toBlock]` pair when set.
 * `steps` is a list of `{ block, step }` records.
 */
data class NetworkStakingStepRewardsOptions(
    val positions: List<Int>? = null,
    val range: Pair<Long, Long>? = null,
    val steps: List<Map<String, Any?>>? = null,
    val stakers: List<String>? = null,
) {
    /** Convert to the wire-format object the JS RPC server expects. */
    fun toWire(): Map<String, Any?> = buildMap {
        if (positions != null) put("positions", positions)
        if (range != null) put("range", listOf(range.first, range.second))
        if (steps != null) put("steps", steps)
        if (stakers != null) put("stakers", stakers)
    }
}

/**
 * Shared 5-method surface of each step-rewards-by-<dimension> viewer. Kotlin
 * expresses each reward tranche as a map keyed by the dimension (position
 * id, staker address, step index, or "total" sentinel) with AttoXL1 values
 * as BigInteger.
 */
interface NetworkStakingStepRewardsViewerMethods : Provider {
    suspend fun bonus(options: NetworkStakingStepRewardsOptions? = null): Map<String, BigInteger>
    suspend fun claimed(options: NetworkStakingStepRewardsOptions? = null): Map<String, BigInteger>
    suspend fun earned(options: NetworkStakingStepRewardsOptions? = null): Map<String, BigInteger>
    suspend fun total(options: NetworkStakingStepRewardsOptions? = null): Map<String, BigInteger>
    suspend fun unclaimed(options: NetworkStakingStepRewardsOptions? = null): Map<String, BigInteger>
}

interface NetworkStakingStepRewardsByPositionViewer : NetworkStakingStepRewardsViewerMethods {
    override val moniker: String get() = MONIKER
    companion object { const val MONIKER = "NetworkStakeStepRewardsByPositionViewer" }
}

interface NetworkStakingStepRewardsByStakerViewer : NetworkStakingStepRewardsViewerMethods {
    override val moniker: String get() = MONIKER
    companion object { const val MONIKER = "NetworkStakeStepRewardsByStakerViewer" }
}

interface NetworkStakingStepRewardsByStepViewer : NetworkStakingStepRewardsViewerMethods {
    override val moniker: String get() = MONIKER
    companion object { const val MONIKER = "NetworkStakeStepRewardsByStepViewer" }
}

interface NetworkStakingStepRewardsTotalViewer : NetworkStakingStepRewardsViewerMethods {
    override val moniker: String get() = MONIKER
    companion object { const val MONIKER = "NetworkStakeStepRewardsTotalViewer" }
}
