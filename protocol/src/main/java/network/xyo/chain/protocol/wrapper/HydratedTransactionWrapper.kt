package network.xyo.chain.protocol.wrapper

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.transaction.HydratedTransaction
import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.client.payload.model.Payload

/**
 * Wrapper for hydrated transactions with convenient accessors,
 * matching JS HydratedTransaction wrapper.
 */
class HydratedTransactionWrapper(val transaction: HydratedTransaction) {

    val boundWitness: TransactionBoundWitness get() = transaction.boundWitness
    val payloads: List<Payload> get() = transaction.payloads

    val from: String get() = boundWitness.from
    val chain: ChainId get() = boundWitness.chain
    val nbf: XL1BlockNumber get() = boundWitness.nbfBlockNumber
    val exp: XL1BlockNumber get() = boundWitness.expBlockNumber
    val fees: FeesWrapper get() = FeesWrapper(boundWitness.fees.toBigInt())
    val scripts: List<String>? get() = boundWitness.script
    val addresses: List<String> get() = boundWitness.addresses
    val signatures: List<String>? get() = boundWitness.signatures
    val timestamp: Long? get() = boundWitness.timestamp

    /** Check if the transaction has expired at the given block. */
    fun isExpiredAt(block: XL1BlockNumber): Boolean {
        return block.value >= boundWitness.exp
    }

    /** Check if the transaction is valid (not before) at the given block. */
    fun isValidAt(block: XL1BlockNumber): Boolean {
        return block.value >= boundWitness.nbf && !isExpiredAt(block)
    }
}
