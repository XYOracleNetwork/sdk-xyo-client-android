package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedBlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
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

    override suspend fun headBlock(): SignedBlockBoundWitnessWithHashMeta {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD_BLOCK, schemas = schemas)
    }

    override suspend fun headHash(): String {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD_HASH, schemas = schemas)
    }

    override suspend fun headNumber(): XL1BlockNumber {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD_NUMBER, schemas = schemas)
    }

    override suspend fun chainId(): ChainId {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_CHAIN_ID, schemas = schemas)
    }
}
