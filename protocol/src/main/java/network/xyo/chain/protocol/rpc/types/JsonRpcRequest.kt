package network.xyo.chain.protocol.rpc.types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val id: String,
    val method: String,
    val params: Any? = emptyList<Any>(),
)
