package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness

class TransactionTransfersValidator : TransactionValidator {
    override fun validate(transaction: TransactionBoundWitness): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (transaction.payload_hashes.size != transaction.payload_schemas.size) {
            errors.add(
                ValidationError(
                    "PAYLOAD_MISMATCH",
                    "payload_hashes length (${transaction.payload_hashes.size}) must match payload_schemas length (${transaction.payload_schemas.size})",
                )
            )
        }

        if (transaction.chain.isBlank()) {
            errors.add(ValidationError("MISSING_CHAIN", "Transaction must specify a chain"))
        }

        return errors
    }
}
