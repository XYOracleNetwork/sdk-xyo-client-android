package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val StakeViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Position>(RpcMethodNames.STAKE_VIEWER_BY_ID)
    method<Position>(RpcMethodNames.STAKE_VIEWER_BY_STAKER)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_BY_STAKED)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_STAKES_BY_STAKER)
    method<Long>(RpcMethodNames.STAKE_VIEWER_MIN_WITHDRAWAL_BLOCKS) { raw ->
        (raw as Number).toLong()
    }
    method<String>(RpcMethodNames.STAKE_VIEWER_REWARDS_CONTRACT) { raw ->
        raw as String
    }
    method<String>(RpcMethodNames.STAKE_VIEWER_STAKING_TOKEN_ADDRESS) { raw ->
        raw as String
    }
}
