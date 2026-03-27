package network.xyo.chain.protocol.sdk.transaction

import network.xyo.chain.protocol.payload.TransferPayload
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.viewer.TransactionViewer
import kotlinx.coroutines.delay

/**
 * Create a transfer payload for use in transactions.
 * Matches JS createTransferPayload().
 */
fun createTransferPayload(
    from: String,
    transfers: Map<String, String>,
    epoch: Long? = null,
    context: Map<String, Any?>? = null,
): TransferPayload {
    return TransferPayload(
        from = from,
        transfers = transfers,
        epoch = epoch,
        context = context,
    )
}

/**
 * Poll for transaction confirmation by hash.
 * Matches JS confirmSubmittedTransaction().
 *
 * @param transactionViewer viewer to query for the transaction
 * @param transactionHash the hash of the submitted transaction
 * @param pollIntervalMs how often to poll (default 2000ms)
 * @param maxAttempts maximum number of polling attempts (default 30)
 * @return the confirmed transaction, or null if not confirmed within the timeout
 */
suspend fun confirmSubmittedTransaction(
    transactionViewer: TransactionViewer,
    transactionHash: String,
    pollIntervalMs: Long = 2000,
    maxAttempts: Int = 30,
): SignedHydratedTransactionWithHashMeta? {
    repeat(maxAttempts) {
        val result = transactionViewer.byHash(transactionHash)
        if (result != null) return result
        delay(pollIntervalMs)
    }
    return null
}
