package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.runner.MempoolRunner
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction

fun rpcMethodHandlersFromMempoolRunner(runner: MempoolRunner): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val transactions = params[0] as List<SignedHydratedTransaction>
        runner.submitTransactions(transactions)
    },
    RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_BLOCKS to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val blocks = params[0] as List<network.xyo.chain.protocol.block.SignedHydratedBlock>
        runner.submitBlocks(blocks)
    },
)
