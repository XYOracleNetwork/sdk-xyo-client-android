package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness

class TransactionFromValidator : TransactionValidator {
    private val addressRegex = Regex("^[0-9a-f]{40}$", RegexOption.IGNORE_CASE)

    override fun validate(transaction: TransactionBoundWitness): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (transaction.from.isBlank()) {
            errors.add(ValidationError("MISSING_FROM", "Transaction must have a 'from' address"))
        } else if (!addressRegex.matches(transaction.from.removePrefix("0x"))) {
            errors.add(ValidationError("INVALID_FROM", "Invalid 'from' address format"))
        }

        if (transaction.addresses.isEmpty()) {
            errors.add(ValidationError("MISSING_ADDRESSES", "Transaction must have at least one signer address"))
        }

        return errors
    }
}
