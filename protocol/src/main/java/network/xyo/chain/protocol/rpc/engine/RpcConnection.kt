package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.runner.MempoolRunner
import network.xyo.chain.protocol.runner.StakeRunner
import network.xyo.chain.protocol.viewer.AccountBalanceViewer
import network.xyo.chain.protocol.viewer.BlockRewardViewer
import network.xyo.chain.protocol.viewer.BlockViewer
import network.xyo.chain.protocol.viewer.FinalizationViewer
import network.xyo.chain.protocol.viewer.MempoolViewer
import network.xyo.chain.protocol.viewer.NetworkStakeViewer
import network.xyo.chain.protocol.viewer.StakeTotalsViewer
import network.xyo.chain.protocol.viewer.StakeViewer
import network.xyo.chain.protocol.viewer.TimeSyncViewer
import network.xyo.chain.protocol.viewer.TransactionViewer
import network.xyo.chain.protocol.viewer.TransferBalanceViewer

/**
 * Aggregates optional viewer/runner instances.
 * Mirrors XyoConnection from xl1-protocol.
 */
data class RpcConnection(
    val blockViewer: BlockViewer? = null,
    val transactionViewer: TransactionViewer? = null,
    val accountBalanceViewer: AccountBalanceViewer? = null,
    val stakeViewer: StakeViewer? = null,
    val finalizationViewer: FinalizationViewer? = null,
    val timeSyncViewer: TimeSyncViewer? = null,
    val networkStakeViewer: NetworkStakeViewer? = null,
    val mempoolViewer: MempoolViewer? = null,
    val mempoolRunner: MempoolRunner? = null,
    val transferBalanceViewer: TransferBalanceViewer? = null,
    val stakeTotalsViewer: StakeTotalsViewer? = null,
    val blockRewardViewer: BlockRewardViewer? = null,
    val stakeRunner: StakeRunner? = null,
)

/**
 * Build a unified handler map from all available viewers/runners in a connection.
 * Mirrors rpcMethodHandlersFromConnection from xl1-protocol.
 */
fun rpcMethodHandlersFromConnection(connection: RpcConnection): RpcMethodHandlerMap = buildMap {
    connection.blockViewer?.let { putAll(rpcMethodHandlersFromBlockViewer(it)) }
    connection.transactionViewer?.let { putAll(rpcMethodHandlersFromTransactionViewer(it)) }
    connection.accountBalanceViewer?.let { putAll(rpcMethodHandlersFromAccountBalanceViewer(it)) }
    connection.stakeViewer?.let { putAll(rpcMethodHandlersFromStakeViewer(it)) }
    connection.finalizationViewer?.let { putAll(rpcMethodHandlersFromFinalizationViewer(it)) }
    connection.timeSyncViewer?.let { putAll(rpcMethodHandlersFromTimeSyncViewer(it)) }
    connection.networkStakeViewer?.let { putAll(rpcMethodHandlersFromNetworkStakeViewer(it)) }
    connection.mempoolViewer?.let { putAll(rpcMethodHandlersFromMempoolViewer(it)) }
    connection.mempoolRunner?.let { putAll(rpcMethodHandlersFromMempoolRunner(it)) }
    connection.transferBalanceViewer?.let { putAll(rpcMethodHandlersFromTransferBalanceViewer(it)) }
    connection.stakeTotalsViewer?.let { putAll(rpcMethodHandlersFromStakeTotalsViewer(it)) }
    connection.blockRewardViewer?.let { putAll(rpcMethodHandlersFromBlockRewardViewer(it)) }
    connection.stakeRunner?.let { putAll(rpcMethodHandlersFromStakeRunner(it)) }
}

/** Convenience: create an RpcEngine from a connection with all registered schemas. */
fun rpcEngineFromConnection(connection: RpcConnection): RpcEngine {
    val handlers = rpcMethodHandlersFromConnection(connection)
    return RpcEngine(
        handlers = handlers,
        schemas = network.xyo.chain.protocol.rpc.schema.AllRpcSchemas,
    )
}
