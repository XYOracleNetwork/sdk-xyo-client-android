package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedBlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val FinalizationViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<SignedHydratedBlockWithHashMeta>(RpcMethodNames.FINALIZATION_VIEWER_HEAD)
    method<SignedBlockBoundWitnessWithHashMeta>(RpcMethodNames.FINALIZATION_VIEWER_HEAD_BLOCK)
    method<String>(RpcMethodNames.FINALIZATION_VIEWER_HEAD_HASH)
    method<XL1BlockNumber>(RpcMethodNames.FINALIZATION_VIEWER_HEAD_NUMBER) { raw ->
        XL1BlockNumber((raw as Number).toLong())
    }
    method<ChainId>(RpcMethodNames.FINALIZATION_VIEWER_CHAIN_ID) { raw ->
        raw as? String ?: error("Chain ID not found")
    }
}
