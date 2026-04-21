package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.transaction.SignedTransactionBoundWitness
import network.xyo.client.payload.Payload

val TransactionViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<SignedHydratedTransactionWithHashMeta?>(RpcMethodNames.TX_VIEWER_BY_HASH) { raw ->
        if (raw == null) null else parseTransactionByHash(raw)
    }
    method<SignedHydratedTransactionWithHashMeta?>(RpcMethodNames.TX_VIEWER_TRANSACTION_BY_HASH) { raw ->
        if (raw == null) null else parseTransactionByHash(raw)
    }
    method<SignedHydratedTransactionWithHashMeta?>(RpcMethodNames.TX_VIEWER_BY_BLOCK_HASH_AND_INDEX) { raw ->
        if (raw == null) null else parseTransactionByHash(raw)
    }
    method<SignedHydratedTransactionWithHashMeta?>(RpcMethodNames.TX_VIEWER_BY_BLOCK_NUMBER_AND_INDEX) { raw ->
        if (raw == null) null else parseTransactionByHash(raw)
    }
}

/**
 * Parse a transaction from the RPC response format: [boundWitness, payloads]
 * The hash is extracted from _hash in the boundWitness map.
 */
@Suppress("UNCHECKED_CAST")
private fun parseTransactionByHash(raw: Any): SignedHydratedTransactionWithHashMeta {
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
