package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.client.account.address.XyoAddress

class TransactionFromValidator : TransactionValidator {
    override fun validate(transaction: TransactionBoundWitness): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (transaction.from.isBlank()) {
            errors.add(ValidationError("MISSING_FROM", "Transaction must have a 'from' address"))
        } else {
            val normalizedFrom = transaction.from.removePrefix("0x")
            if (!XyoAddress.isAddress(normalizedFrom) && !XyoAddress.isAddress(transaction.from)) {
                errors.add(ValidationError("INVALID_FROM", "Invalid 'from' address format"))
            }
        }

        if (transaction.addresses.isEmpty()) {
            errors.add(ValidationError("MISSING_ADDRESSES", "Transaction must have at least one signer address"))
        }

        return errors
    }
}
