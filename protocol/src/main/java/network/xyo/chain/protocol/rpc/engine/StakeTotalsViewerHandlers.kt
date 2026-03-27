package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.StakeTotalsViewer

fun rpcMethodHandlersFromStakeTotalsViewer(viewer: StakeTotalsViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE to RpcMethodHandler { params ->
        viewer.active(params.getOrNull(0) as? Long)
    },
    RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKED to RpcMethodHandler { params ->
        viewer.activeByStaked(params[0] as String, params.getOrNull(1) as? Long)
    },
    RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKER to RpcMethodHandler { params ->
        viewer.activeByStaker(params[0] as String, params.getOrNull(1) as? Long)
    },
    RpcMethodNames.STAKE_TOTALS_VIEWER_PENDING to RpcMethodHandler { params ->
        viewer.pending(params.getOrNull(0) as? Long)
    },
    RpcMethodNames.STAKE_TOTALS_VIEWER_PENDING_BY_STAKER to RpcMethodHandler { params ->
        viewer.pendingByStaker(params[0] as String, params.getOrNull(1) as? Long)
    },
    RpcMethodNames.STAKE_TOTALS_VIEWER_WITHDRAWN to RpcMethodHandler { params ->
        viewer.withdrawn(params.getOrNull(0) as? Long)
    },
    RpcMethodNames.STAKE_TOTALS_VIEWER_WITHDRAWN_BY_STAKER to RpcMethodHandler { params ->
        viewer.withdrawnByStaker(params[0] as String, params.getOrNull(1) as? Long)
    },
)
