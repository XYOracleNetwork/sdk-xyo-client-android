package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TimeSyncViewer

class JsonRpcTimeSyncViewer(
    private val transport: RpcTransport,
) : TimeSyncViewer {
    override val moniker: String = TimeSyncViewer.MONIKER

    override suspend fun serverTime(): Long {
        val result = transport.sendRequest(RpcMethodNames.TIME_SYNC_VIEWER_SERVER_TIME)
        return (result as Number).toLong()
    }
}
