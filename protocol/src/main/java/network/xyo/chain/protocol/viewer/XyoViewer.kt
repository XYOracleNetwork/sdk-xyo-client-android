package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.runner.StakeRunner
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.client.payload.model.Payload
import java.math.BigInteger

/**
 * Aggregated viewer matching JS XyoViewer.
 *
 * Composes all sub-viewers and runners into a single entry point for
 * querying chain state. This is the primary developer-facing class for
 * reading protocol data.
 *
 * Usage:
 * ```kotlin
 * val viewer = XyoViewer(
 *     block = jsonRpcBlockViewer,
 *     transaction = jsonRpcTransactionViewer,
 *     // ...
 * )
 * val currentBlock = viewer.block.currentBlock()
 * val balance = viewer.transferBalance.transferBalance(address)
 * ```
 */
class XyoViewer(
    val block: BlockViewer? = null,
    val transaction: TransactionViewer? = null,
    val accountBalance: AccountBalanceViewer? = null,
    val stake: StakeViewer? = null,
    val stakeTotals: StakeTotalsViewer? = null,
    val finalization: FinalizationViewer? = null,
    val timeSync: TimeSyncViewer? = null,
    val networkStake: NetworkStakeViewer? = null,
    val mempool: MempoolViewer? = null,
    val transferBalance: TransferBalanceViewer? = null,
    val blockReward: BlockRewardViewer? = null,
    val step: StepViewer? = null,
    val stepRewards: NetworkStakeStepRewardsViewer? = null,
    val stakeRunner: StakeRunner? = null,
) {
    // Convenience delegation methods for the most common operations

    /** Get blocks starting from a hash. */
    suspend fun blocksByHash(hash: String, limit: Int? = null): List<SignedHydratedBlockWithHashMeta> {
        return block?.blocksByHash(hash, limit) ?: emptyList()
    }

    /** Get blocks starting from a block number. */
    suspend fun blocksByNumber(blockNumber: XL1BlockNumber, limit: Int? = null): List<SignedHydratedBlockWithHashMeta> {
        return block?.blocksByNumber(blockNumber, limit) ?: emptyList()
    }

    /** Get the current (latest) block. */
    suspend fun currentBlock(): SignedHydratedBlockWithHashMeta? {
        return block?.currentBlock()
    }

    /** Get payloads by their hashes. */
    suspend fun payloadsByHash(hashes: List<String>): List<Payload> {
        return block?.payloadsByHash(hashes) ?: emptyList()
    }

    /** Get a transaction by its hash. */
    suspend fun transactionByHash(hash: String): SignedHydratedTransactionWithHashMeta? {
        return transaction?.byHash(hash)
    }

    /** Get the transfer balance for an address. */
    suspend fun balance(address: String): AttoXL1? {
        return transferBalance?.transferBalance(address)
    }

    /** Get the finalized head block. */
    suspend fun head(): SignedHydratedBlockWithHashMeta? {
        return finalization?.head()
    }

    /** Get pending transactions from the mempool. */
    suspend fun pendingTransactions(options: PendingTransactionsOptions? = null): List<SignedHydratedTransactionWithHashMeta> {
        return mempool?.pendingTransactions(options) ?: emptyList()
    }

    /** Get the active network stake. */
    suspend fun activeStake(blockNumber: XL1BlockNumber? = null): ActiveStakeResult? {
        return networkStake?.active(blockNumber)
    }
}
