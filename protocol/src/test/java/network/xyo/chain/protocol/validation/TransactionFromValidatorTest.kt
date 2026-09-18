package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.chain.protocol.transaction.TransactionFeesHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TransactionFromValidatorTest {

    private val validator = TransactionFromValidator()

    private fun stubTx(from: String) = TransactionBoundWitness(
        from = from,
        chain = "c5fe2e6f6841cbab12d8c0618be2df8c6156cc44",
        nbf = 0L,
        exp = 100L,
        fees = TransactionFeesHex("0x0", "0x5208", "0x1", "0x0"),
        addresses = listOf("5e7a847447e7fec41011ae7d32d768f86605ba03"),
    )

    @Test
    fun `accepts legacy 40 hex address with and without 0x`() {
        val tx1 = stubTx("5e7a847447e7fec41011ae7d32d768f86605ba03")
        assertTrue(validator.validate(tx1).isEmpty())

        val tx2 = stubTx("0x5e7a847447e7fec41011ae7d32d768f86605ba03")
        assertTrue(validator.validate(tx2).isEmpty())
    }

    @Test
    fun `accepts post-quantum qm65 address`() {
        val quantAddress = "qm651qyyq79says4nyw2qga892hrrdfchsluxleee2j"
        val tx = stubTx(quantAddress)
        assertTrue(validator.validate(tx).isEmpty())
    }

    @Test
    fun `rejects invalid address format`() {
        val tx = stubTx("not-an-address")
        val errors = validator.validate(tx)
        assertEquals(1, errors.size)
        assertEquals("INVALID_FROM", errors.first().code)
    }

    @Test
    fun `rejects blank address`() {
        val tx = stubTx("")
        val errors = validator.validate(tx)
        assertTrue(errors.any { it.code == "MISSING_FROM" })
    }
}
