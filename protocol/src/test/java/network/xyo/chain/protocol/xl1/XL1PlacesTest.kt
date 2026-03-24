package network.xyo.chain.protocol.xl1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class XL1PlacesTest {

    @Test
    fun `places have correct values`() {
        assertEquals(0, XL1Places.atto)
        assertEquals(3, XL1Places.femto)
        assertEquals(6, XL1Places.pico)
        assertEquals(9, XL1Places.nano)
        assertEquals(12, XL1Places.micro)
        assertEquals(15, XL1Places.milli)
        assertEquals(18, XL1Places.xl1)
    }

    @Test
    fun `all places list contains all denominations`() {
        assertEquals(7, XL1Places.all.size)
        assertEquals(listOf(0, 3, 6, 9, 12, 15, 18), XL1Places.all)
    }

    @Test
    fun `conversion factors are correct powers of 10`() {
        assertEquals(BigInteger.ONE, AttoXL1ConvertFactor.atto)
        assertEquals(BigInteger.TEN.pow(3), AttoXL1ConvertFactor.femto)
        assertEquals(BigInteger.TEN.pow(6), AttoXL1ConvertFactor.pico)
        assertEquals(BigInteger.TEN.pow(9), AttoXL1ConvertFactor.nano)
        assertEquals(BigInteger.TEN.pow(12), AttoXL1ConvertFactor.micro)
        assertEquals(BigInteger.TEN.pow(15), AttoXL1ConvertFactor.milli)
        assertEquals(BigInteger.TEN.pow(18), AttoXL1ConvertFactor.xl1)
    }

    @Test
    fun `xl1MaxValue calculates correctly`() {
        // atto: 10^32 - 1
        assertEquals(BigInteger.TEN.pow(32) - BigInteger.ONE, xl1MaxValue(XL1Places.atto))
        // xl1: 10^14 - 1
        assertEquals(BigInteger.TEN.pow(14) - BigInteger.ONE, xl1MaxValue(XL1Places.xl1))
        // milli: 10^17 - 1
        assertEquals(BigInteger.TEN.pow(17) - BigInteger.ONE, xl1MaxValue(XL1Places.milli))
    }
}
