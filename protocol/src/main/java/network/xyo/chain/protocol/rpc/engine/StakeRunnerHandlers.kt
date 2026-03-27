package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.runner.StakeRunner
import java.math.BigInteger

fun rpcMethodHandlersFromStakeRunner(runner: StakeRunner): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.STAKE_RUNNER_ADD_STAKE to RpcMethodHandler { params ->
        runner.addStake(params[0] as String, BigInteger(params[1].toString()))
    },
    RpcMethodNames.STAKE_RUNNER_REMOVE_STAKE to RpcMethodHandler { params ->
        runner.removeStake((params[0] as Number).toLong())
    },
    RpcMethodNames.STAKE_RUNNER_WITHDRAW_STAKE to RpcMethodHandler { params ->
        runner.withdrawStake((params[0] as Number).toLong())
    },
)
