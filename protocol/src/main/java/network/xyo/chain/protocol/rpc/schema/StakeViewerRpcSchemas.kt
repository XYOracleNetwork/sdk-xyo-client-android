package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val StakeViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Position>(RpcMethodNames.STAKE_VIEWER_BY_ID)
    method<Position>(RpcMethodNames.STAKE_VIEWER_BY_STAKER)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_BY_STAKED)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_STAKES_BY_STAKER)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_ACTIVE)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_REMOVED)
    method<List<Position>>(RpcMethodNames.STAKE_VIEWER_WITHDRAWN)
}
