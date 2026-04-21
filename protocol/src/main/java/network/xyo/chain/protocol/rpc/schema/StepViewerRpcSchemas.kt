package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import java.math.BigInteger

val StepViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Int>(RpcMethodNames.STEP_VIEWER_POSITION_COUNT) { raw -> (raw as Number).toInt() }
    method<List<Position>>(RpcMethodNames.STEP_VIEWER_POSITIONS)
    method<BigInteger>(RpcMethodNames.STEP_VIEWER_RANDOMIZER) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STEP_VIEWER_STAKE) { raw -> BigInteger(raw.toString()) }
    method<Int>(RpcMethodNames.STEP_VIEWER_STAKER_COUNT) { raw -> (raw as Number).toInt() }
    method<List<String>>(RpcMethodNames.STEP_VIEWER_STAKERS)
    method<BigInteger>(RpcMethodNames.STEP_VIEWER_WEIGHT) { raw -> BigInteger(raw.toString()) }
}

val NetworkStakingStepRewardsViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {}

val LegacyStepRewardsViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<BigInteger>(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_BY_POSITION) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_BY_STAKER) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_BY_STEP) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_TOTAL) { raw -> BigInteger(raw.toString()) }
}
