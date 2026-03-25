package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedBlockBoundWitness
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.client.payload.Payload
import network.xyo.client.payload.model.Payload as PayloadInterface

val BlockViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<SignedHydratedBlockWithHashMeta>>(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_HASH) { raw ->
        parseBlockList(raw)
    }
    method<List<SignedHydratedBlockWithHashMeta>>(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_NUMBER) { raw ->
        parseBlockList(raw)
    }
    method<SignedHydratedBlockWithHashMeta>(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK) { raw ->
        parseBlock(raw)
    }
    method<List<PayloadInterface>>(RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH)
}

@Suppress("UNCHECKED_CAST")
private fun parseBlockList(raw: Any?): List<SignedHydratedBlockWithHashMeta> {
    val list = raw as? List<Any?> ?: return emptyList()
    return list.map { parseBlock(it) }
}

/**
 * Parse a block from the RPC response format: [boundWitness, payloads]
 * The hash is extracted from _hash in the boundWitness map.
 */
@Suppress("UNCHECKED_CAST")
private fun parseBlock(raw: Any?): SignedHydratedBlockWithHashMeta {
    val tuple = raw as? List<Any?> ?: error("Expected block tuple [boundWitness, payloads]")
    val bwMap = tuple[0] as Map<String, Any?>
    val payloadsList = tuple[1] as? List<Map<String, Any?>> ?: emptyList()
    val hash = bwMap["_hash"] as? String ?: ""
    val boundWitness = rpcMoshi.adapter(SignedBlockBoundWitness::class.java).fromJsonValue(bwMap)
        ?: error("Failed to deserialize SignedBlockBoundWitness")
    val payloads = payloadsList.map { payloadMap ->
        rpcMoshi.adapter(Payload::class.java).fromJsonValue(payloadMap)
            ?: Payload(payloadMap["schema"] as? String ?: "unknown")
    }
    return SignedHydratedBlockWithHashMeta(boundWitness, payloads, hash)
}
