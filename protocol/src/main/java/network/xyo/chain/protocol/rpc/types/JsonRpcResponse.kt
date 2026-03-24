package network.xyo.chain.protocol.rpc.types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: Any? = null,
)

@JsonClass(generateAdapter = true)
data class JsonRpcResponse(
    val jsonrpc: String = "2.0",
    val id: String? = null,
    val result: Any? = null,
    val error: JsonRpcError? = null,
) {
    val isSuccess: Boolean get() = error == null
    val isError: Boolean get() = error != null
}

object JsonRpcErrorCodes {
    const val PARSE_ERROR = -32700
    const val INVALID_REQUEST = -32600
    const val METHOD_NOT_FOUND = -32601
    const val INVALID_PARAMS = -32602
    const val INTERNAL_ERROR = -32603
}
