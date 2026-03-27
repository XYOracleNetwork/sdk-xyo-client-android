package network.xyo.chain.protocol.rpc.runner

import network.xyo.chain.protocol.rpc.schema.StakeRunnerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.runner.StakeRunner
import java.math.BigInteger

class JsonRpcStakeRunner(
    private val transport: RpcTransport,
) : StakeRunner {
    override val moniker: String = StakeRunner.MONIKER

    private val schemas = StakeRunnerRpcSchemas

    override suspend fun addStake(staked: String, amount: BigInteger): Boolean {
        return transport.sendRequest(RpcMethodNames.STAKE_RUNNER_ADD_STAKE, listOf(staked, amount.toString()), schemas)
    }

    override suspend fun removeStake(slot: Long): Boolean {
        return transport.sendRequest(RpcMethodNames.STAKE_RUNNER_REMOVE_STAKE, listOf(slot), schemas)
    }

    override suspend fun withdrawStake(slot: Long): Boolean {
        return transport.sendRequest(RpcMethodNames.STAKE_RUNNER_WITHDRAW_STAKE, listOf(slot), schemas)
    }
}
