package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.TimeSyncViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TimeDomain
import network.xyo.chain.protocol.viewer.TimeSyncViewer

class JsonRpcTimeSyncViewer(
    private val transport: RpcTransport,
) : TimeSyncViewer {
    override val moniker: String = TimeSyncViewer.MONIKER

    private val schemas = TimeSyncViewerRpcSchemas

    override suspend fun convertTime(from: TimeDomain, to: TimeDomain, value: Long): Long {
        return transport.sendRequest(
            RpcMethodNames.TIME_SYNC_VIEWER_CONVERT_TIME,
            listOf(from.name, to.name, value),
            schemas,
        )
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun currentTime(domain: TimeDomain): Pair<TimeDomain, Long> {
        val result: List<Any?> = transport.sendRequest(
            RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME,
            listOf(domain.name),
            schemas,
        )
        return Pair(TimeDomain.valueOf(result[0] as String), (result[1] as Number).toLong())
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun currentTimeAndHash(domain: TimeDomain): Pair<Long, String?> {
        val result: List<Any?> = transport.sendRequest(
            RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME_AND_HASH,
            listOf(domain.name),
            schemas,
        )
        return Pair((result[0] as Number).toLong(), result.getOrNull(1) as? String)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun currentTimePayload(): Map<String, Any?> {
        return transport.sendRequest(
            RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME_PAYLOAD,
            schemas = schemas,
        )
    }
}
