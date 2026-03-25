package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedBlockBoundWitness
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.transaction.SignedTransactionBoundWitness
import network.xyo.client.payload.Payload

val MempoolViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<SignedHydratedTransactionWithHashMeta>>(RpcMethodNames.MEMPOOL_VIEWER_PENDING_TRANSACTIONS) { raw ->
        parseTransactionList(raw)
    }
    method<List<SignedHydratedBlockWithHashMeta>>(RpcMethodNames.MEMPOOL_VIEWER_PENDING_BLOCKS) { raw ->
        parseMempoolBlockList(raw)
    }
}

@Suppress("UNCHECKED_CAST")
private fun parseTransactionList(raw: Any?): List<SignedHydratedTransactionWithHashMeta> {
    val list = raw as? List<Any?> ?: return emptyList()
    return list.map { parseTransaction(it) }
}

@Suppress("UNCHECKED_CAST")
private fun parseTransaction(raw: Any?): SignedHydratedTransactionWithHashMeta {
    val tuple = raw as? List<Any?> ?: error("Expected transaction tuple [boundWitness, payloads]")
    val bwMap = tuple[0] as Map<String, Any?>
    val payloadsList = tuple[1] as? List<Map<String, Any?>> ?: emptyList()
    val hash = bwMap["_hash"] as? String ?: ""
    val boundWitness = rpcMoshi.adapter(SignedTransactionBoundWitness::class.java).fromJsonValue(bwMap)
        ?: error("Failed to deserialize SignedTransactionBoundWitness")
    val payloads = payloadsList.map { payloadMap ->
        rpcMoshi.adapter(Payload::class.java).fromJsonValue(payloadMap)
            ?: Payload(payloadMap["schema"] as? String ?: "unknown")
    }
    return SignedHydratedTransactionWithHashMeta(boundWitness, payloads, hash)
}

@Suppress("UNCHECKED_CAST")
private fun parseMempoolBlockList(raw: Any?): List<SignedHydratedBlockWithHashMeta> {
    val list = raw as? List<Any?> ?: return emptyList()
    return list.map { parseMempoolBlock(it) }
}

@Suppress("UNCHECKED_CAST")
private fun parseMempoolBlock(raw: Any?): SignedHydratedBlockWithHashMeta {
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
