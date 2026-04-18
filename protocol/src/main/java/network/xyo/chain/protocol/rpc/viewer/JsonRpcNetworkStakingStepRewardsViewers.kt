package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.NetworkStakingStepRewardsByPositionViewerRpcSchemas
import network.xyo.chain.protocol.rpc.schema.NetworkStakingStepRewardsByStakerViewerRpcSchemas
import network.xyo.chain.protocol.rpc.schema.NetworkStakingStepRewardsByStepViewerRpcSchemas
import network.xyo.chain.protocol.rpc.schema.NetworkStakingStepRewardsTotalViewerRpcSchemas
import network.xyo.chain.protocol.rpc.schema.RpcSchemaMap
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.NetworkStakingStepRewardsByPositionViewer
import network.xyo.chain.protocol.viewer.NetworkStakingStepRewardsByStakerViewer
import network.xyo.chain.protocol.viewer.NetworkStakingStepRewardsByStepViewer
import network.xyo.chain.protocol.viewer.NetworkStakingStepRewardsOptions
import network.xyo.chain.protocol.viewer.NetworkStakingStepRewardsTotalViewer
import java.math.BigInteger

/**
 * Shared implementation of the 5-method step-rewards surface. Each concrete
 * viewer binds it to a fixed set of RPC method strings and a schema map; the
 * wire protocol is identical across dimensions, only the method names differ.
 */
private class StepRewardsCall(
    private val transport: RpcTransport,
    private val schemas: RpcSchemaMap,
    private val bonusMethod: String,
    private val claimedMethod: String,
    private val earnedMethod: String,
    private val totalMethod: String,
    private val unclaimedMethod: String,
) {
    suspend fun call(method: String, options: NetworkStakingStepRewardsOptions?): Map<String, BigInteger> {
        val params: List<Any?> = if (options == null) emptyList() else listOf(options.toWire())
        return transport.sendRequest(method, params, schemas)
    }

    suspend fun bonus(options: NetworkStakingStepRewardsOptions?) = call(bonusMethod, options)
    suspend fun claimed(options: NetworkStakingStepRewardsOptions?) = call(claimedMethod, options)
    suspend fun earned(options: NetworkStakingStepRewardsOptions?) = call(earnedMethod, options)
    suspend fun total(options: NetworkStakingStepRewardsOptions?) = call(totalMethod, options)
    suspend fun unclaimed(options: NetworkStakingStepRewardsOptions?) = call(unclaimedMethod, options)
}

class JsonRpcNetworkStakingStepRewardsByPositionViewer(
    transport: RpcTransport,
) : NetworkStakingStepRewardsByPositionViewer {
    override val moniker: String = NetworkStakingStepRewardsByPositionViewer.MONIKER
    private val call = StepRewardsCall(
        transport,
        NetworkStakingStepRewardsByPositionViewerRpcSchemas,
        RpcMethodNames.REWARDS_BY_POSITION_BONUS,
        RpcMethodNames.REWARDS_BY_POSITION_CLAIMED,
        RpcMethodNames.REWARDS_BY_POSITION_EARNED,
        RpcMethodNames.REWARDS_BY_POSITION_TOTAL,
        RpcMethodNames.REWARDS_BY_POSITION_UNCLAIMED,
    )
    override suspend fun bonus(options: NetworkStakingStepRewardsOptions?) = call.bonus(options)
    override suspend fun claimed(options: NetworkStakingStepRewardsOptions?) = call.claimed(options)
    override suspend fun earned(options: NetworkStakingStepRewardsOptions?) = call.earned(options)
    override suspend fun total(options: NetworkStakingStepRewardsOptions?) = call.total(options)
    override suspend fun unclaimed(options: NetworkStakingStepRewardsOptions?) = call.unclaimed(options)
}

class JsonRpcNetworkStakingStepRewardsByStakerViewer(
    transport: RpcTransport,
) : NetworkStakingStepRewardsByStakerViewer {
    override val moniker: String = NetworkStakingStepRewardsByStakerViewer.MONIKER
    private val call = StepRewardsCall(
        transport,
        NetworkStakingStepRewardsByStakerViewerRpcSchemas,
        RpcMethodNames.REWARDS_BY_STAKER_BONUS,
        RpcMethodNames.REWARDS_BY_STAKER_CLAIMED,
        RpcMethodNames.REWARDS_BY_STAKER_EARNED,
        RpcMethodNames.REWARDS_BY_STAKER_TOTAL,
        RpcMethodNames.REWARDS_BY_STAKER_UNCLAIMED,
    )
    override suspend fun bonus(options: NetworkStakingStepRewardsOptions?) = call.bonus(options)
    override suspend fun claimed(options: NetworkStakingStepRewardsOptions?) = call.claimed(options)
    override suspend fun earned(options: NetworkStakingStepRewardsOptions?) = call.earned(options)
    override suspend fun total(options: NetworkStakingStepRewardsOptions?) = call.total(options)
    override suspend fun unclaimed(options: NetworkStakingStepRewardsOptions?) = call.unclaimed(options)
}

class JsonRpcNetworkStakingStepRewardsByStepViewer(
    transport: RpcTransport,
) : NetworkStakingStepRewardsByStepViewer {
    override val moniker: String = NetworkStakingStepRewardsByStepViewer.MONIKER
    private val call = StepRewardsCall(
        transport,
        NetworkStakingStepRewardsByStepViewerRpcSchemas,
        RpcMethodNames.REWARDS_BY_STEP_BONUS,
        RpcMethodNames.REWARDS_BY_STEP_CLAIMED,
        RpcMethodNames.REWARDS_BY_STEP_EARNED,
        RpcMethodNames.REWARDS_BY_STEP_TOTAL,
        RpcMethodNames.REWARDS_BY_STEP_UNCLAIMED,
    )
    override suspend fun bonus(options: NetworkStakingStepRewardsOptions?) = call.bonus(options)
    override suspend fun claimed(options: NetworkStakingStepRewardsOptions?) = call.claimed(options)
    override suspend fun earned(options: NetworkStakingStepRewardsOptions?) = call.earned(options)
    override suspend fun total(options: NetworkStakingStepRewardsOptions?) = call.total(options)
    override suspend fun unclaimed(options: NetworkStakingStepRewardsOptions?) = call.unclaimed(options)
}

class JsonRpcNetworkStakingStepRewardsTotalViewer(
    transport: RpcTransport,
) : NetworkStakingStepRewardsTotalViewer {
    override val moniker: String = NetworkStakingStepRewardsTotalViewer.MONIKER
    private val call = StepRewardsCall(
        transport,
        NetworkStakingStepRewardsTotalViewerRpcSchemas,
        RpcMethodNames.REWARDS_TOTAL_BONUS,
        RpcMethodNames.REWARDS_TOTAL_CLAIMED,
        RpcMethodNames.REWARDS_TOTAL_EARNED,
        RpcMethodNames.REWARDS_TOTAL_TOTAL,
        RpcMethodNames.REWARDS_TOTAL_UNCLAIMED,
    )
    override suspend fun bonus(options: NetworkStakingStepRewardsOptions?) = call.bonus(options)
    override suspend fun claimed(options: NetworkStakingStepRewardsOptions?) = call.claimed(options)
    override suspend fun earned(options: NetworkStakingStepRewardsOptions?) = call.earned(options)
    override suspend fun total(options: NetworkStakingStepRewardsOptions?) = call.total(options)
    override suspend fun unclaimed(options: NetworkStakingStepRewardsOptions?) = call.unclaimed(options)
}
