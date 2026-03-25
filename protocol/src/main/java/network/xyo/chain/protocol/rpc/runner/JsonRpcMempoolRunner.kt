package network.xyo.chain.protocol.rpc.runner

import network.xyo.chain.protocol.block.SignedHydratedBlock
import network.xyo.chain.protocol.rpc.schema.MempoolRunnerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.runner.MempoolPruneOptions
import network.xyo.chain.protocol.runner.MempoolRunner
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction

class JsonRpcMempoolRunner(
    private val transport: RpcTransport,
) : MempoolRunner {
    override val moniker: String = MempoolRunner.MONIKER

    private val schemas = MempoolRunnerRpcSchemas

    override suspend fun submitTransactions(transactions: List<SignedHydratedTransaction>): List<String> {
        return transport.sendRequest(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS, listOf(transactions), schemas)
    }

    override suspend fun submitBlocks(blocks: List<SignedHydratedBlock>): List<String> {
        return transport.sendRequest(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_BLOCKS, listOf(blocks), schemas)
    }

    override suspend fun prunePendingTransactions(options: MempoolPruneOptions?): Pair<Int, Int> {
        // TODO: implement when RPC method is available
        return Pair(0, 0)
    }

    override suspend fun prunePendingBlocks(options: MempoolPruneOptions?): Pair<Int, Int> {
        // TODO: implement when RPC method is available
        return Pair(0, 0)
    }
}
