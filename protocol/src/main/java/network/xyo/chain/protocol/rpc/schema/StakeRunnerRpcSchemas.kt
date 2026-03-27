package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val StakeRunnerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Boolean>(RpcMethodNames.STAKE_RUNNER_ADD_STAKE) { raw -> raw as Boolean }
    method<Boolean>(RpcMethodNames.STAKE_RUNNER_REMOVE_STAKE) { raw -> raw as Boolean }
    method<Boolean>(RpcMethodNames.STAKE_RUNNER_WITHDRAW_STAKE) { raw -> raw as Boolean }
}
