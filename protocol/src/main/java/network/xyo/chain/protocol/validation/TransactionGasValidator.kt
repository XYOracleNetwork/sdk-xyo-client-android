package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import java.math.BigInteger

class TransactionGasValidator : TransactionValidator {
    override fun validate(transaction: TransactionBoundWitness): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        val fees = transaction.fees.toBigInt()

        if (fees.gasLimit <= BigInteger.ZERO) {
            errors.add(ValidationError("INVALID_GAS_LIMIT", "gasLimit must be positive"))
        }

        if (fees.gasPrice < BigInteger.ZERO) {
            errors.add(ValidationError("INVALID_GAS_PRICE", "gasPrice must be non-negative"))
        }

        if (fees.base < BigInteger.ZERO) {
            errors.add(ValidationError("INVALID_BASE_FEE", "base fee must be non-negative"))
        }

        if (fees.priority < BigInteger.ZERO) {
            errors.add(ValidationError("INVALID_PRIORITY", "priority fee must be non-negative"))
        }

        return errors
    }
}
