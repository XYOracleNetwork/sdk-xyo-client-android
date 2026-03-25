package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val MempoolRunnerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<String>>(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS)
    method<List<String>>(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_BLOCKS)
}
