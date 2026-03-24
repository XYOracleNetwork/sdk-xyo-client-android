package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.StakeViewer

class JsonRpcStakeViewer(
    private val transport: RpcTransport,
) : StakeViewer {
    override val moniker: String = StakeViewer.MONIKER

    override suspend fun stakeById(id: PositionId): Position {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_BY_ID, listOf(id))
        // TODO: deserialize
        error("Not yet implemented")
    }

    override suspend fun stakeByStaker(staker: String, slot: Int): Position {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_BY_STAKER, listOf(staker, slot))
        // TODO: deserialize
        error("Not yet implemented")
    }

    override suspend fun stakesByStaked(staked: String): List<Position> {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_BY_STAKED, listOf(staked))
        // TODO: deserialize
        return emptyList()
    }

    override suspend fun stakesByStaker(staker: String): List<Position> {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_STAKES_BY_STAKER, listOf(staker))
        // TODO: deserialize
        return emptyList()
    }

    override suspend fun activeStakes(): List<Position> {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_ACTIVE)
        // TODO: deserialize
        return emptyList()
    }

    override suspend fun removedStakes(): List<Position> {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_REMOVED)
        // TODO: deserialize
        return emptyList()
    }

    override suspend fun withdrawnStakes(): List<Position> {
        transport.sendRequest(RpcMethodNames.STAKE_VIEWER_WITHDRAWN)
        // TODO: deserialize
        return emptyList()
    }

    override suspend fun minWithdrawalBlocks(): Long {
        val result = transport.sendRequest("stakeViewer_minWithdrawalBlocks")
        return (result as Number).toLong()
    }

    override suspend fun rewardsContract(): String {
        return transport.sendRequest("stakeViewer_rewardsContract") as String
    }

    override suspend fun stakingTokenAddress(): String {
        return transport.sendRequest("stakeViewer_stakingTokenAddress") as String
    }
}
