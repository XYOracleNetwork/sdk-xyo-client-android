package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.DataLakeViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.DataLakeViewer

class JsonRpcDataLakeViewer(
    private val transport: RpcTransport,
) : DataLakeViewer {
    override val moniker: String = DataLakeViewer.MONIKER

    private val schemas = DataLakeViewerRpcSchemas

    override suspend fun get(hashes: List<String>): List<Map<String, Any?>> {
        return transport.sendRequest(RpcMethodNames.DATA_LAKE_VIEWER_GET, listOf(hashes), schemas)
    }

    override suspend fun next(options: Map<String, Any?>): List<Map<String, Any?>> {
        return transport.sendRequest(RpcMethodNames.DATA_LAKE_VIEWER_NEXT, listOf(options), schemas)
    }
}
