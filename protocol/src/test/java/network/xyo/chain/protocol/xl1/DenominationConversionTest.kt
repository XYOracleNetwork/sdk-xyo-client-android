package network.xyo.chain.protocol.xl1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class DenominationConversionTest {

    @Test
    fun `XL1 toAtto converts correctly`() {
        val oneXL1 = XL1(BigInteger.ONE)
        val atto = oneXL1.toAtto()
        assertEquals(BigInteger.TEN.pow(18), atto.value)
    }

    @Test
    fun `MilliXL1 toAtto converts correctly`() {
        val oneMilli = MilliXL1(BigInteger.ONE)
        val atto = oneMilli.toAtto()
        assertEquals(BigInteger.TEN.pow(15), atto.value)
    }

    @Test
    fun `MicroXL1 toAtto converts correctly`() {
        val oneMicro = MicroXL1(BigInteger.ONE)
        val atto = oneMicro.toAtto()
        assertEquals(BigInteger.TEN.pow(12), atto.value)
    }

    @Test
    fun `NanoXL1 toAtto converts correctly`() {
        val oneNano = NanoXL1(BigInteger.ONE)
        val atto = oneNano.toAtto()
        assertEquals(BigInteger.TEN.pow(9), atto.value)
    }

    @Test
    fun `PicoXL1 toAtto converts correctly`() {
        val onePico = PicoXL1(BigInteger.ONE)
        val atto = onePico.toAtto()
        assertEquals(BigInteger.TEN.pow(6), atto.value)
    }

    @Test
    fun `FemtoXL1 toAtto converts correctly`() {
        val oneFemto = FemtoXL1(BigInteger.ONE)
        val atto = oneFemto.toAtto()
        assertEquals(BigInteger.TEN.pow(3), atto.value)
    }

    @Test
    fun `each denomination has correct max value`() {
        assertEquals(BigInteger.TEN.pow(32) - BigInteger.ONE, AttoXL1.MAX_VALUE)
        assertEquals(BigInteger.TEN.pow(29) - BigInteger.ONE, FemtoXL1.MAX_VALUE)
        assertEquals(BigInteger.TEN.pow(26) - BigInteger.ONE, PicoXL1.MAX_VALUE)
        assertEquals(BigInteger.TEN.pow(23) - BigInteger.ONE, NanoXL1.MAX_VALUE)
        assertEquals(BigInteger.TEN.pow(20) - BigInteger.ONE, MicroXL1.MAX_VALUE)
        assertEquals(BigInteger.TEN.pow(17) - BigInteger.ONE, MilliXL1.MAX_VALUE)
        assertEquals(BigInteger.TEN.pow(14) - BigInteger.ONE, XL1.MAX_VALUE)
    }

    @Test
    fun `ZERO values are zero for all denominations`() {
        assertEquals(BigInteger.ZERO, AttoXL1.ZERO.value)
        assertEquals(BigInteger.ZERO, FemtoXL1.ZERO.value)
        assertEquals(BigInteger.ZERO, PicoXL1.ZERO.value)
        assertEquals(BigInteger.ZERO, NanoXL1.ZERO.value)
        assertEquals(BigInteger.ZERO, MicroXL1.ZERO.value)
        assertEquals(BigInteger.ZERO, MilliXL1.ZERO.value)
        assertEquals(BigInteger.ZERO, XL1.ZERO.value)
    }

    @Test
    fun `round trip XL1 to atto and back`() {
        val original = BigInteger.valueOf(42)
        val xl1 = XL1(original)
        val atto = xl1.toAtto()
        val backToXl1 = XL1(atto.value / AttoXL1ConvertFactor.xl1)
        assertEquals(original, backToXl1.value)
    }
}
