package network.xyo.chain.protocol.rpc.transport

interface RpcTransport {
    suspend fun sendRequest(method: String, params: List<Any?> = emptyList()): Any?
}
