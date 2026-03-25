package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.TimeSyncViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TimeSyncViewer

class JsonRpcTimeSyncViewer(
    private val transport: RpcTransport,
) : TimeSyncViewer {
    override val moniker: String = TimeSyncViewer.MONIKER

    private val schemas = TimeSyncViewerRpcSchemas

    override suspend fun serverTime(): Long {
        return transport.sendRequest(RpcMethodNames.TIME_SYNC_VIEWER_SERVER_TIME, schemas = schemas)
    }
}
