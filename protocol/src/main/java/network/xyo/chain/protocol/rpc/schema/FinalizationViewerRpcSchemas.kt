package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedBlockBoundWitness
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.client.payload.Payload

val FinalizationViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<SignedHydratedBlockWithHashMeta>(RpcMethodNames.FINALIZATION_VIEWER_HEAD) { raw ->
        parseFinalizationBlock(raw)
    }
}

/**
 * Parse a block from the RPC response format: [boundWitness, payloads]
 * The hash is extracted from _hash in the boundWitness map.
 */
@Suppress("UNCHECKED_CAST")
private fun parseFinalizationBlock(raw: Any?): SignedHydratedBlockWithHashMeta {
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
