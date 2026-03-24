package network.xyo.chain.protocol.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class TransactionFeesTest {

    @Test
    fun `TransactionFeesHex toBigInt converts correctly`() {
        val hex = TransactionFeesHex(
            base = "0xa",
            gasLimit = "0x5208",
            gasPrice = "0x1",
            priority = "0x0",
        )
        val bigInt = hex.toBigInt()
        assertEquals(BigInteger.TEN, bigInt.base)
        assertEquals(BigInteger.valueOf(21000), bigInt.gasLimit)
        assertEquals(BigInteger.ONE, bigInt.gasPrice)
        assertEquals(BigInteger.ZERO, bigInt.priority)
    }

    @Test
    fun `TransactionFeesBigInt toHex converts correctly`() {
        val bigInt = TransactionFeesBigInt(
            base = BigInteger.TEN,
            gasLimit = BigInteger.valueOf(21000),
            gasPrice = BigInteger.ONE,
            priority = BigInteger.ZERO,
        )
        val hex = bigInt.toHex()
        assertEquals("0xa", hex.base)
        assertEquals("0x5208", hex.gasLimit)
        assertEquals("0x1", hex.gasPrice)
        assertEquals("0x0", hex.priority)
    }

    @Test
    fun `round trip hex to bigint and back`() {
        val original = TransactionFeesHex(
            base = "0xff",
            gasLimit = "0x5208",
            gasPrice = "0x3b9aca00",
            priority = "0x59682f00",
        )
        val roundTripped = original.toBigInt().toHex()
        assertEquals(original, roundTripped)
    }

    @Test
    fun `fromHex static method works`() {
        val hex = TransactionFeesHex("0x0", "0x5208", "0x1", "0x0")
        val bigInt = TransactionFeesBigInt.fromHex(hex)
        assertEquals(BigInteger.ZERO, bigInt.base)
        assertEquals(BigInteger.valueOf(21000), bigInt.gasLimit)
    }
}
