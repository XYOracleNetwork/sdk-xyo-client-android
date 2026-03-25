package network.xyo.chain.protocol.rpc.engine

/** A handler for a single RPC method. Receives params as a list and returns a result. */
fun interface RpcMethodHandler<TResult> {
    suspend fun handle(params: List<Any?>): TResult
}

/** Untyped handler map used by the engine. */
typealias RpcMethodHandlerMap = Map<String, RpcMethodHandler<*>>
