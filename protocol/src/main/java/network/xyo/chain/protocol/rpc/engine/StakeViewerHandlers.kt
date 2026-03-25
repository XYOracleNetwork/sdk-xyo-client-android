package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.StakeViewer

fun rpcMethodHandlersFromStakeViewer(viewer: StakeViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.STAKE_VIEWER_BY_ID to RpcMethodHandler { params ->
        viewer.stakeById((params[0] as Number).toInt())
    },
    RpcMethodNames.STAKE_VIEWER_BY_STAKER to RpcMethodHandler { params ->
        viewer.stakeByStaker(params[0] as String, (params[1] as Number).toInt())
    },
    RpcMethodNames.STAKE_VIEWER_BY_STAKED to RpcMethodHandler { params ->
        viewer.stakesByStaked(params[0] as String)
    },
    RpcMethodNames.STAKE_VIEWER_STAKES_BY_STAKER to RpcMethodHandler { params ->
        viewer.stakesByStaker(params[0] as String)
    },
    RpcMethodNames.STAKE_VIEWER_MIN_WITHDRAWAL_BLOCKS to RpcMethodHandler {
        viewer.minWithdrawalBlocks()
    },
    RpcMethodNames.STAKE_VIEWER_REWARDS_CONTRACT to RpcMethodHandler {
        viewer.rewardsContract()
    },
    RpcMethodNames.STAKE_VIEWER_STAKING_TOKEN_ADDRESS to RpcMethodHandler {
        viewer.stakingTokenAddress()
    },
)
