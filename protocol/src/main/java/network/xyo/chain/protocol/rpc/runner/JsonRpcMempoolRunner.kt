package network.xyo.chain.protocol.rpc.runner

import network.xyo.chain.protocol.block.SignedHydratedBlock
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.runner.MempoolPruneOptions
import network.xyo.chain.protocol.runner.MempoolRunner
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction

class JsonRpcMempoolRunner(
    private val transport: RpcTransport,
) : MempoolRunner {
    override val moniker: String = MempoolRunner.MONIKER

    @Suppress("UNCHECKED_CAST")
    override suspend fun submitTransactions(transactions: List<SignedHydratedTransaction>): List<String> {
        val result = transport.sendRequest(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS, listOf(transactions))
        return result as? List<String> ?: emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun submitBlocks(blocks: List<SignedHydratedBlock>): List<String> {
        val result = transport.sendRequest(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_BLOCKS, listOf(blocks))
        return result as? List<String> ?: emptyList()
    }

    override suspend fun prunePendingTransactions(options: MempoolPruneOptions?): Pair<Int, Int> {
        // TODO: implement
        return Pair(0, 0)
    }

    override suspend fun prunePendingBlocks(options: MempoolPruneOptions?): Pair<Int, Int> {
        // TODO: implement
        return Pair(0, 0)
    }
}
