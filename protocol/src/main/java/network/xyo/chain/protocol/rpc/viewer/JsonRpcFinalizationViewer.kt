package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.schema.FinalizationViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.FinalizationViewer

class JsonRpcFinalizationViewer(
    private val transport: RpcTransport,
) : FinalizationViewer {
    override val moniker: String = FinalizationViewer.MONIKER

    private val schemas = FinalizationViewerRpcSchemas

    override suspend fun head(): SignedHydratedBlockWithHashMeta {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD, schemas = schemas)
    }
}
