package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedHydratedBlock
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction

val MempoolRunnerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<String>>(
        RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS,
        paramsTransform = { raw -> raw as? List<Any?> ?: emptyList() },
        paramsSerialize = { params -> serializeSubmitTransactionsParams(params) },
    )
    method<List<String>>(
        RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_BLOCKS,
        paramsTransform = { raw -> raw as? List<Any?> ?: emptyList() },
        paramsSerialize = { params -> serializeSubmitBlocksParams(params) },
    )
}

private fun serializeSubmitTransactionsParams(params: List<Any?>): Any? {
    val transactions = (params.firstOrNull() as? List<*>).orEmpty()
        .mapNotNull { it as? SignedHydratedTransaction }
        .map { transaction ->
            listOf(
                toJsonValue(transaction.boundWitness),
                transaction.payloads.map { payload -> toJsonValue(payload) },
            )
        }
    return listOf(transactions)
}

private fun serializeSubmitBlocksParams(params: List<Any?>): Any? {
    val blocks = (params.firstOrNull() as? List<*>).orEmpty()
        .mapNotNull { it as? SignedHydratedBlock }
        .map { block ->
            listOf(
                toJsonValue(block.boundWitness),
                block.payloads.map { payload -> toJsonValue(payload) },
            )
        }
    return listOf(blocks)
}

private fun toJsonValue(value: Any?): Any? {
    @Suppress("UNCHECKED_CAST")
    val adapter = rpcMoshi.adapter(value?.javaClass ?: Any::class.java) as com.squareup.moshi.JsonAdapter<Any?>
    return adapter.toJsonValue(value)
}
