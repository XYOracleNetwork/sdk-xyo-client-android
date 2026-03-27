package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import java.math.BigInteger

val StepViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Int>(RpcMethodNames.NETWORK_STAKE_STEP_POSITION_COUNT) { raw -> (raw as Number).toInt() }
    method<List<Position>>(RpcMethodNames.NETWORK_STAKE_STEP_POSITIONS)
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_STEP_RANDOMIZER) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_STEP_STAKE) { raw -> BigInteger(raw.toString()) }
    method<Int>(RpcMethodNames.NETWORK_STAKE_STEP_STAKER_COUNT) { raw -> (raw as Number).toInt() }
    method<List<String>>(RpcMethodNames.NETWORK_STAKE_STEP_STAKERS)
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_STEP_WEIGHT) { raw -> BigInteger(raw.toString()) }
}

val StepRewardsViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_REWARDS_BY_POSITION) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_REWARDS_BY_STAKER) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_REWARDS_BY_STEP) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_REWARDS_TOTAL) { raw -> BigInteger(raw.toString()) }
}
