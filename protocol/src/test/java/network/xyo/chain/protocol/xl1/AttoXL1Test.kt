package network.xyo.chain.protocol.xl1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

class AttoXL1Test {

    @Test
    fun `creates from valid value`() {
        val amount = AttoXL1(BigInteger.valueOf(1000))
        assertEquals(BigInteger.valueOf(1000), amount.value)
    }

    @Test
    fun `ZERO is zero`() {
        assertEquals(BigInteger.ZERO, AttoXL1.ZERO.value)
    }

    @Test
    fun `rejects negative value`() {
        assertThrows<IllegalArgumentException> {
            AttoXL1(BigInteger.valueOf(-1))
        }
    }

    @Test
    fun `rejects value exceeding max`() {
        assertThrows<IllegalArgumentException> {
            AttoXL1(AttoXL1.MAX_VALUE + BigInteger.ONE)
        }
    }

    @Test
    fun `accepts max value`() {
        val amount = AttoXL1(AttoXL1.MAX_VALUE)
        assertEquals(AttoXL1.MAX_VALUE, amount.value)
    }

    @Test
    fun `addition works correctly`() {
        val a = AttoXL1(BigInteger.valueOf(100))
        val b = AttoXL1(BigInteger.valueOf(200))
        assertEquals(BigInteger.valueOf(300), (a + b).value)
    }

    @Test
    fun `subtraction works correctly`() {
        val a = AttoXL1(BigInteger.valueOf(300))
        val b = AttoXL1(BigInteger.valueOf(100))
        assertEquals(BigInteger.valueOf(200), (a - b).value)
    }

    @Test
    fun `comparison works correctly`() {
        val a = AttoXL1(BigInteger.valueOf(100))
        val b = AttoXL1(BigInteger.valueOf(200))
        assert(a < b)
        assert(b > a)
        assertEquals(0, a.compareTo(AttoXL1(BigInteger.valueOf(100))))
    }

    @Test
    fun `of factory creates instance`() {
        val amount = AttoXL1.of(BigInteger.TEN)
        assertEquals(BigInteger.TEN, amount.value)
    }

    @Test
    fun `ofOrNull returns null for invalid`() {
        assertNull(AttoXL1.ofOrNull(BigInteger.valueOf(-1)))
    }

    @Test
    fun `ofOrNull returns value for valid`() {
        assertNotNull(AttoXL1.ofOrNull(BigInteger.TEN))
    }

    @Test
    fun `fromHex parses hex string`() {
        val amount = AttoXL1.fromHex("0xff")
        assertEquals(BigInteger.valueOf(255), amount.value)
    }

    @Test
    fun `fromHex parses without prefix`() {
        val amount = AttoXL1.fromHex("ff")
        assertEquals(BigInteger.valueOf(255), amount.value)
    }

    @Test
    fun `toHex produces correct output`() {
        val amount = AttoXL1(BigInteger.valueOf(255))
        assertEquals("0xff", amount.toHex())
    }

    @Test
    fun `fromHexOrNull returns null for invalid`() {
        assertNull(AttoXL1.fromHexOrNull("not_hex"))
    }

    @Test
    fun `toString returns decimal string`() {
        val amount = AttoXL1(BigInteger.valueOf(12345))
        assertEquals("12345", amount.toString())
    }
}
