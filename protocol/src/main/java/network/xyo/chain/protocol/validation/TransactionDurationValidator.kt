package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness

class TransactionDurationValidator : TransactionValidator {
    override fun validate(transaction: TransactionBoundWitness): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (transaction.nbf < 0) {
            errors.add(ValidationError("INVALID_NBF", "nbf (not before block) must be non-negative"))
        }

        if (transaction.exp < 0) {
            errors.add(ValidationError("INVALID_EXP", "exp (expiration block) must be non-negative"))
        }

        if (transaction.exp <= transaction.nbf) {
            errors.add(ValidationError("INVALID_DURATION", "exp must be greater than nbf"))
        }

        return errors
    }
}
