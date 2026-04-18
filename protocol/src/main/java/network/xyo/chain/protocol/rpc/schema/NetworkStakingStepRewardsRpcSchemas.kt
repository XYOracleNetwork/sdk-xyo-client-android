package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import java.math.BigInteger

/**
 * Shared result parser for step-rewards viewers. The JS wire format is
 * `Record<K, bigint-as-hex-string>` — e.g. `{"0":"0x1a2b","1":"0x0"}` — and
 * we normalize to `Map<String, BigInteger>` on the Kotlin side. Keys are
 * JSON object keys (always strings) so callers can index however the
 * dimension semantically wants (position id, staker address, step number).
 */
@Suppress("UNCHECKED_CAST")
internal fun parseRewardsMap(raw: Any?): Map<String, BigInteger> {
    val map = raw as? Map<String, Any?> ?: return emptyMap()
    return map.mapValues { (_, v) -> parseAttoXL1(v) }
}

private fun parseAttoXL1(value: Any?): BigInteger {
    return when (value) {
        null -> BigInteger.ZERO
        is String -> {
            val trimmed = value.removePrefix("0x").removePrefix("0X")
            if (trimmed.isEmpty()) BigInteger.ZERO else BigInteger(trimmed, 16)
        }
        is Number -> BigInteger.valueOf(value.toLong())
        else -> BigInteger(value.toString())
    }
}

private fun rewardsSchemas(
    bonus: String, claimed: String, earned: String, total: String, unclaimed: String,
): RpcSchemaMap = rpcSchemaMap {
    method<Map<String, BigInteger>>(bonus) { raw -> parseRewardsMap(raw) }
    method<Map<String, BigInteger>>(claimed) { raw -> parseRewardsMap(raw) }
    method<Map<String, BigInteger>>(earned) { raw -> parseRewardsMap(raw) }
    method<Map<String, BigInteger>>(total) { raw -> parseRewardsMap(raw) }
    method<Map<String, BigInteger>>(unclaimed) { raw -> parseRewardsMap(raw) }
}

val NetworkStakingStepRewardsByPositionViewerRpcSchemas: RpcSchemaMap = rewardsSchemas(
    bonus = RpcMethodNames.REWARDS_BY_POSITION_BONUS,
    claimed = RpcMethodNames.REWARDS_BY_POSITION_CLAIMED,
    earned = RpcMethodNames.REWARDS_BY_POSITION_EARNED,
    total = RpcMethodNames.REWARDS_BY_POSITION_TOTAL,
    unclaimed = RpcMethodNames.REWARDS_BY_POSITION_UNCLAIMED,
)

val NetworkStakingStepRewardsByStakerViewerRpcSchemas: RpcSchemaMap = rewardsSchemas(
    bonus = RpcMethodNames.REWARDS_BY_STAKER_BONUS,
    claimed = RpcMethodNames.REWARDS_BY_STAKER_CLAIMED,
    earned = RpcMethodNames.REWARDS_BY_STAKER_EARNED,
    total = RpcMethodNames.REWARDS_BY_STAKER_TOTAL,
    unclaimed = RpcMethodNames.REWARDS_BY_STAKER_UNCLAIMED,
)

val NetworkStakingStepRewardsByStepViewerRpcSchemas: RpcSchemaMap = rewardsSchemas(
    bonus = RpcMethodNames.REWARDS_BY_STEP_BONUS,
    claimed = RpcMethodNames.REWARDS_BY_STEP_CLAIMED,
    earned = RpcMethodNames.REWARDS_BY_STEP_EARNED,
    total = RpcMethodNames.REWARDS_BY_STEP_TOTAL,
    unclaimed = RpcMethodNames.REWARDS_BY_STEP_UNCLAIMED,
)

val NetworkStakingStepRewardsTotalViewerRpcSchemas: RpcSchemaMap = rewardsSchemas(
    bonus = RpcMethodNames.REWARDS_TOTAL_BONUS,
    claimed = RpcMethodNames.REWARDS_TOTAL_CLAIMED,
    earned = RpcMethodNames.REWARDS_TOTAL_EARNED,
    total = RpcMethodNames.REWARDS_TOTAL_TOTAL,
    unclaimed = RpcMethodNames.REWARDS_TOTAL_UNCLAIMED,
)
