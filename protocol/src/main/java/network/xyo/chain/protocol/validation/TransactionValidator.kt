package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness

fun interface TransactionValidator {
    fun validate(transaction: TransactionBoundWitness): List<ValidationError>
}

data class ValidationError(
    val code: String,
    val message: String,
)

fun validateTransaction(transaction: TransactionBoundWitness, validators: List<TransactionValidator>): List<ValidationError> {
    return validateSdkBoundWitness(transaction, transaction) + validators.flatMap { it.validate(transaction) }
}
