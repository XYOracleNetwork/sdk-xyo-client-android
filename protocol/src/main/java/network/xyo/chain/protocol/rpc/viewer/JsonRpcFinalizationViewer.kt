package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedBlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.FinalizationViewer

class JsonRpcFinalizationViewer(
    private val transport: RpcTransport,
) : FinalizationViewer {
    override val moniker: String = FinalizationViewer.MONIKER

    override suspend fun head(): SignedHydratedBlockWithHashMeta {
        transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD)
        // TODO: deserialize
        error("Not yet implemented")
    }

    override suspend fun headBlock(): SignedBlockBoundWitnessWithHashMeta {
        transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD_BLOCK)
        // TODO: deserialize
        error("Not yet implemented")
    }

    override suspend fun headHash(): String {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD_HASH) as? String
            ?: error("Head hash not found")
    }

    override suspend fun headNumber(): XL1BlockNumber {
        val result = transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_HEAD_NUMBER)
        return XL1BlockNumber((result as Number).toLong())
    }

    override suspend fun chainId(): ChainId {
        return transport.sendRequest(RpcMethodNames.FINALIZATION_VIEWER_CHAIN_ID) as? String
            ?: error("Chain ID not found")
    }
}
