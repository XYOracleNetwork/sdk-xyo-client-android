package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.rpc.schema.LegacyStepRewardsViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.NetworkStakeStepRewardsViewer
import java.math.BigInteger

class JsonRpcStepRewardsViewer(
    private val transport: RpcTransport,
) : NetworkStakeStepRewardsViewer {
    override val moniker: String = NetworkStakeStepRewardsViewer.MONIKER

    private val schemas = LegacyStepRewardsViewerRpcSchemas

    override suspend fun byPosition(positionId: PositionId): BigInteger {
        return transport.sendRequest(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_BY_POSITION, listOf(positionId), schemas)
    }

    override suspend fun byStaker(staker: String): BigInteger {
        return transport.sendRequest(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_BY_STAKER, listOf(staker), schemas)
    }

    override suspend fun byStep(step: Int): BigInteger {
        return transport.sendRequest(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_BY_STEP, listOf(step), schemas)
    }

    override suspend fun total(): BigInteger {
        return transport.sendRequest(RpcMethodNames.LEGACY_NETWORK_STAKE_REWARDS_TOTAL, schemas = schemas)
    }
}
