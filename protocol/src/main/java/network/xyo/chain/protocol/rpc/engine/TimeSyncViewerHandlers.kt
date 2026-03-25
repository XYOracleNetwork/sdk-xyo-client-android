package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TimeDomain
import network.xyo.chain.protocol.viewer.TimeSyncViewer

fun rpcMethodHandlersFromTimeSyncViewer(viewer: TimeSyncViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.TIME_SYNC_VIEWER_CONVERT_TIME to RpcMethodHandler { params ->
        val from = TimeDomain.valueOf(params[0] as String)
        val to = TimeDomain.valueOf(params[1] as String)
        val value = (params[2] as Number).toLong()
        viewer.convertTime(from, to, value)
    },
    RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME to RpcMethodHandler { params ->
        val domain = TimeDomain.valueOf(params[0] as String)
        val result = viewer.currentTime(domain)
        listOf(result.first.name, result.second)
    },
    RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME_AND_HASH to RpcMethodHandler { params ->
        val domain = TimeDomain.valueOf(params[0] as String)
        val result = viewer.currentTimeAndHash(domain)
        listOf(result.first, result.second)
    },
    RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME_PAYLOAD to RpcMethodHandler {
        viewer.currentTimePayload()
    },
)
