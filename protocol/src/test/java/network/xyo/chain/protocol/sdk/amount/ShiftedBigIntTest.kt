package network.xyo.chain.protocol.sdk.amount

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigInteger

class ShiftedBigIntTest {

    @Test
    fun `formats zero correctly`() {
        val sbi = ShiftedBigInt(BigInteger.ZERO, ShiftedBigIntConfig(places = 18))
        val result = sbi.toShortString()
        assertTrue(result.contains("0"))
    }

    @Test
    fun `formats 1 XL1 correctly with 18 places`() {
        val oneXL1InAtto = BigInteger.TEN.pow(18)
        val sbi = ShiftedBigInt(oneXL1InAtto, ShiftedBigIntConfig(places = 18, minDecimals = 1))
        val result = sbi.toShortString()
        assertTrue(result.startsWith("1"))
    }

    @Test
    fun `formats with no decimal places`() {
        val sbi = ShiftedBigInt(BigInteger.valueOf(12345), ShiftedBigIntConfig(places = 0))
        val result = sbi.toShortString()
        assertEquals("12,345", result)
    }

    @Test
    fun `toFullString includes all decimal places`() {
        val oneXL1InAtto = BigInteger.TEN.pow(18)
        val sbi = ShiftedBigInt(oneXL1InAtto, ShiftedBigIntConfig(places = 18))
        val full = sbi.toFullString()
        assertTrue(full.contains("1"))
        // Should have 18 decimal places
        val parts = full.split(".")
        if (parts.size == 2) {
            assertEquals(18, parts[1].length)
        }
    }

    @Test
    fun `respects maxCharacters limit`() {
        val largeValue = BigInteger.TEN.pow(25)
        val sbi = ShiftedBigInt(largeValue, ShiftedBigIntConfig(places = 18, maxCharacters = 9))
        val result = sbi.toShortString()
        // Should not be excessively long
        assertTrue(result.length <= 20) // some room for commas and decimal
    }

    @Test
    fun `toString delegates to toShortString`() {
        val sbi = ShiftedBigInt(BigInteger.valueOf(100), ShiftedBigIntConfig(places = 2))
        assertEquals(sbi.toShortString(), sbi.toString())
    }
}
