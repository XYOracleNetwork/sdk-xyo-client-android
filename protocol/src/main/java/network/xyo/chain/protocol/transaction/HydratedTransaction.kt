package network.xyo.chain.protocol.transaction

import network.xyo.client.payload.model.Payload

data class HydratedTransaction(
    val boundWitness: TransactionBoundWitness,
    val payloads: List<Payload>,
)

data class SignedHydratedTransaction(
    val boundWitness: SignedTransactionBoundWitness,
    val payloads: List<Payload>,
)

data class HydratedTransactionWithHashMeta(
    val boundWitness: TransactionBoundWitness,
    val payloads: List<Payload>,
    val hash: String,
)

data class SignedHydratedTransactionWithHashMeta(
    val boundWitness: SignedTransactionBoundWitness,
    val payloads: List<Payload>,
    val hash: String,
)
