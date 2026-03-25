package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.rpc.schema.StakeViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.StakeViewer

class JsonRpcStakeViewer(
    private val transport: RpcTransport,
) : StakeViewer {
    override val moniker: String = StakeViewer.MONIKER

    private val schemas = StakeViewerRpcSchemas

    override suspend fun stakeById(id: PositionId): Position {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_BY_ID, listOf(id), schemas)
    }

    override suspend fun stakeByStaker(staker: String, slot: Int): Position {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_BY_STAKER, listOf(staker, slot), schemas)
    }

    override suspend fun stakesByStaked(staked: String): List<Position> {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_BY_STAKED, listOf(staked), schemas)
    }

    override suspend fun stakesByStaker(staker: String): List<Position> {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_STAKES_BY_STAKER, listOf(staker), schemas)
    }

    override suspend fun minWithdrawalBlocks(): Long {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_MIN_WITHDRAWAL_BLOCKS, schemas = schemas)
    }

    override suspend fun rewardsContract(): String {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_REWARDS_CONTRACT, schemas = schemas)
    }

    override suspend fun stakingTokenAddress(): String {
        return transport.sendRequest(RpcMethodNames.STAKE_VIEWER_STAKING_TOKEN_ADDRESS, schemas = schemas)
    }
}
