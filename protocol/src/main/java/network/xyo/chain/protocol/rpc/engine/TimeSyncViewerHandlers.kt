package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TimeSyncViewer

fun rpcMethodHandlersFromTimeSyncViewer(viewer: TimeSyncViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.TIME_SYNC_VIEWER_SERVER_TIME to RpcMethodHandler { viewer.serverTime() },
)
