package network.xyo.chain.protocol.transaction

import network.xyo.chain.protocol.block.XL1BlockNumber
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TransactionBoundWitnessTest {

    private val sampleFees = TransactionFeesHex(
        base = "0x0",
        gasLimit = "0x5208",
        gasPrice = "0x1",
        priority = "0x0",
    )

    @Test
    fun `creates with required fields`() {
        val tx = TransactionBoundWitness(
            from = "abc123",
            chain = "def456",
            nbf = 100L,
            exp = 200L,
            fees = sampleFees,
        )
        assertEquals("abc123", tx.from)
        assertEquals("def456", tx.chain)
        assertEquals(100L, tx.nbf)
        assertEquals(200L, tx.exp)
    }

    @Test
    fun `nbfBlockNumber converts correctly`() {
        val tx = TransactionBoundWitness(
            from = "abc123", chain = "def456", nbf = 42L, exp = 100L, fees = sampleFees,
        )
        assertEquals(XL1BlockNumber(42L), tx.nbfBlockNumber)
    }

    @Test
    fun `expBlockNumber converts correctly`() {
        val tx = TransactionBoundWitness(
            from = "abc123", chain = "def456", nbf = 42L, exp = 100L, fees = sampleFees,
        )
        assertEquals(XL1BlockNumber(100L), tx.expBlockNumber)
    }

    @Test
    fun `schema has correct default`() {
        val tx = TransactionBoundWitness(
            from = "abc123", chain = "def456", nbf = 0L, exp = 100L, fees = sampleFees,
        )
        assertEquals("network.xyo.boundwitness", tx.schema)
    }

    @Test
    fun `script defaults to null`() {
        val tx = TransactionBoundWitness(
            from = "abc123", chain = "def456", nbf = 0L, exp = 100L, fees = sampleFees,
        )
        assertNull(tx.script)
    }

    @Test
    fun `script can be set`() {
        val tx = TransactionBoundWitness(
            from = "abc123", chain = "def456", nbf = 0L, exp = 100L, fees = sampleFees,
            script = listOf("transfer", "stake"),
        )
        assertEquals(listOf("transfer", "stake"), tx.script)
    }
}
