package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.schema.RpcSchemaMap
import network.xyo.chain.protocol.rpc.schema.rpcMoshi
import network.xyo.chain.protocol.rpc.types.JsonRpcError
import network.xyo.chain.protocol.rpc.types.JsonRpcErrorCodes
import network.xyo.chain.protocol.rpc.types.JsonRpcRequest
import network.xyo.chain.protocol.rpc.types.JsonRpcResponse

/**
 * A JSON-RPC 2.0 engine that routes requests to typed handlers with schema validation.
 * Mirrors the rpcEngineFromConnection pattern from xl1-protocol.
 */
class RpcEngine(
    private val handlers: RpcMethodHandlerMap,
    private val schemas: RpcSchemaMap,
) {
    suspend fun handle(request: JsonRpcRequest): JsonRpcResponse {
        val method = request.method
        val handler = handlers[method]
            ?: return errorResponse(request.id, JsonRpcErrorCodes.METHOD_NOT_FOUND, "Method not found: $method")

        val schema = schemas[method]
            ?: return errorResponse(request.id, JsonRpcErrorCodes.METHOD_NOT_FOUND, "No schema for method: $method")

        return try {
            @Suppress("UNCHECKED_CAST")
            val params = request.params as? List<Any?> ?: emptyList()
            val result = handler.handle(params)
            val serialized = schema.serializeResult(rpcMoshi, result)
            JsonRpcResponse(id = request.id, result = serialized)
        } catch (e: IllegalArgumentException) {
            errorResponse(request.id, JsonRpcErrorCodes.INVALID_PARAMS, e.message ?: "Invalid params")
        } catch (e: Exception) {
            errorResponse(request.id, JsonRpcErrorCodes.INTERNAL_ERROR, e.message ?: "Internal error")
        }
    }

    private fun errorResponse(id: String, code: Int, message: String): JsonRpcResponse {
        return JsonRpcResponse(
            id = id,
            error = JsonRpcError(code = code, message = message),
        )
    }
}
