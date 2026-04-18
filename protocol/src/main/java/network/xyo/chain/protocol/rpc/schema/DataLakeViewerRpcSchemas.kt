package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val DataLakeViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<Map<String, Any?>>>(RpcMethodNames.DATA_LAKE_VIEWER_GET) { raw -> parseLoosePayloads(raw) }
    method<List<Map<String, Any?>>>(RpcMethodNames.DATA_LAKE_VIEWER_NEXT) { raw -> parseLoosePayloads(raw) }
}

/**
 * The DataLake wire schema is `PayloadZod.loose()` — extra fields beyond
 * `schema` are preserved and we forward them untouched. Moshi's built-in
 * Map deserialization gives us the full field set; the caller decides how
 * to interpret them.
 */
@Suppress("UNCHECKED_CAST")
private fun parseLoosePayloads(raw: Any?): List<Map<String, Any?>> {
    val list = raw as? List<Any?> ?: return emptyList()
    return list.mapNotNull { item -> item as? Map<String, Any?> }
}
