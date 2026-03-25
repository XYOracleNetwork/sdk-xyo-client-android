package network.xyo.chain.protocol.rpc.transport

import network.xyo.chain.protocol.rpc.schema.RpcSchema
import network.xyo.chain.protocol.rpc.schema.RpcSchemaMap
import network.xyo.chain.protocol.rpc.schema.rpcMoshi

interface RpcTransport {
    /** Send an untyped RPC request, returning the raw deserialized result. */
    suspend fun sendRawRequest(method: String, params: List<Any?> = emptyList()): Any?
}

/**
 * Send a typed RPC request, using the schema to deserialize the result.
 * Mirrors the typed sendRequest pattern from xl1-protocol's RpcTransport.
 */
suspend fun <TResult> RpcTransport.sendRequest(
    method: String,
    params: List<Any?> = emptyList(),
    schema: RpcSchema<TResult>,
): TResult {
    val raw = sendRawRequest(method, params)
    return schema.parseResult(rpcMoshi, raw)
}

/**
 * Send a typed RPC request, looking up the schema by method name.
 * Throws if the method is not found in the schema map.
 */
suspend fun <TResult> RpcTransport.sendRequest(
    method: String,
    params: List<Any?> = emptyList(),
    schemas: RpcSchemaMap,
): TResult {
    val schema = schemas[method]
        ?: throw RpcTransportException("No schema registered for method: $method")
    val raw = sendRawRequest(method, params)
    @Suppress("UNCHECKED_CAST")
    return (schema as RpcSchema<TResult>).parseResult(rpcMoshi, raw)
}
