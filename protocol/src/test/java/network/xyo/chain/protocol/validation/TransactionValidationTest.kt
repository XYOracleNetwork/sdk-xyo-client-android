package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.chain.protocol.transaction.TransactionFeesHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TransactionValidationTest {

    private val validFees = TransactionFeesHex(
        base = "0x0",
        gasLimit = "0x5208",
        gasPrice = "0x1",
        priority = "0x0",
    )

    private fun validTransaction() = TransactionBoundWitness(
        from = "abcdef1234567890abcdef1234567890abcdef12",
        chain = "chain123",
        nbf = 100L,
        exp = 200L,
        fees = validFees,
        addresses = listOf("abcdef1234567890abcdef1234567890abcdef12"),
        payload_hashes = listOf("hash1"),
        payload_schemas = listOf("schema1"),
    )

    // Duration Validator

    @Test
    fun `duration validator passes for valid range`() {
        val errors = TransactionDurationValidator().validate(validTransaction())
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `duration validator catches exp lte nbf`() {
        val tx = validTransaction().copy(nbf = 200L, exp = 100L)
        val errors = TransactionDurationValidator().validate(tx)
        assertTrue(errors.any { it.code == "INVALID_DURATION" })
    }

    @Test
    fun `duration validator catches negative nbf`() {
        val tx = validTransaction().copy(nbf = -1L)
        val errors = TransactionDurationValidator().validate(tx)
        assertTrue(errors.any { it.code == "INVALID_NBF" })
    }

    @Test
    fun `duration validator catches equal nbf and exp`() {
        val tx = validTransaction().copy(nbf = 100L, exp = 100L)
        val errors = TransactionDurationValidator().validate(tx)
        assertTrue(errors.any { it.code == "INVALID_DURATION" })
    }

    // From Validator

    @Test
    fun `from validator passes for valid address`() {
        val errors = TransactionFromValidator().validate(validTransaction())
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `from validator catches blank from`() {
        val tx = validTransaction().copy(from = "")
        val errors = TransactionFromValidator().validate(tx)
        assertTrue(errors.any { it.code == "MISSING_FROM" })
    }

    @Test
    fun `from validator catches invalid address format`() {
        val tx = validTransaction().copy(from = "not_an_address")
        val errors = TransactionFromValidator().validate(tx)
        assertTrue(errors.any { it.code == "INVALID_FROM" })
    }

    @Test
    fun `from validator catches missing addresses`() {
        val tx = validTransaction().copy(addresses = emptyList())
        val errors = TransactionFromValidator().validate(tx)
        assertTrue(errors.any { it.code == "MISSING_ADDRESSES" })
    }

    // Gas Validator

    @Test
    fun `gas validator passes for valid fees`() {
        val errors = TransactionGasValidator().validate(validTransaction())
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `gas validator catches zero gas limit`() {
        val tx = validTransaction().copy(fees = validFees.copy(gasLimit = "0x0"))
        val errors = TransactionGasValidator().validate(tx)
        assertTrue(errors.any { it.code == "INVALID_GAS_LIMIT" })
    }

    // Transfers Validator

    @Test
    fun `transfers validator passes when hashes match schemas`() {
        val errors = TransactionTransfersValidator().validate(validTransaction())
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `transfers validator catches mismatched hashes and schemas`() {
        val tx = validTransaction().copy(
            payload_hashes = listOf("hash1", "hash2"),
            payload_schemas = listOf("schema1"),
        )
        val errors = TransactionTransfersValidator().validate(tx)
        assertTrue(errors.any { it.code == "PAYLOAD_MISMATCH" })
    }

    @Test
    fun `transfers validator catches blank chain`() {
        val tx = validTransaction().copy(chain = "")
        val errors = TransactionTransfersValidator().validate(tx)
        assertTrue(errors.any { it.code == "MISSING_CHAIN" })
    }

    // Combined Validation

    @Test
    fun `validateTransaction runs all validators`() {
        val tx = validTransaction()
        val validators = listOf(
            TransactionDurationValidator(),
            TransactionFromValidator(),
            TransactionGasValidator(),
            TransactionTransfersValidator(),
        )
        val errors = validateTransaction(tx, validators)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateTransaction collects errors from multiple validators`() {
        val tx = validTransaction().copy(
            from = "",
            nbf = 200L,
            exp = 100L,
        )
        val validators = listOf(
            TransactionDurationValidator(),
            TransactionFromValidator(),
        )
        val errors = validateTransaction(tx, validators)
        assertEquals(2, errors.size)
    }
}
