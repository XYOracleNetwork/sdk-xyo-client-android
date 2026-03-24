package network.xyo.chain.protocol.sdk.amount

import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.chain.protocol.xl1.FemtoXL1
import network.xyo.chain.protocol.xl1.MilliXL1
import network.xyo.chain.protocol.xl1.XL1
import network.xyo.chain.protocol.xl1.XL1Places
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class XL1AmountTest {

    @Test
    fun `fromAtto stores correct value`() {
        val amount = XL1Amount.fromAtto(BigInteger.valueOf(1000))
        assertEquals(BigInteger.valueOf(1000), amount.atto.value)
    }

    @Test
    fun `fromXL1 converts to atto correctly`() {
        val oneXL1 = XL1(BigInteger.ONE)
        val amount = XL1Amount.fromXL1(oneXL1)
        assertEquals(BigInteger.TEN.pow(18), amount.atto.value)
    }

    @Test
    fun `xl1 property converts back correctly`() {
        val amount = XL1Amount.fromAtto(BigInteger.TEN.pow(18))
        assertEquals(BigInteger.ONE, amount.xl1.value)
    }

    @Test
    fun `milli property converts correctly`() {
        val amount = XL1Amount.fromAtto(BigInteger.TEN.pow(15))
        assertEquals(BigInteger.ONE, amount.milli.value)
    }

    @Test
    fun `from with places converts correctly`() {
        val amount = XL1Amount.from(BigInteger.valueOf(5), XL1Places.xl1)
        assertEquals(BigInteger.valueOf(5) * BigInteger.TEN.pow(18), amount.atto.value)
    }

    @Test
    fun `ZERO has zero value`() {
        assertEquals(BigInteger.ZERO, XL1Amount.ZERO.atto.value)
    }

    @Test
    fun `addition works`() {
        val a = XL1Amount.fromAtto(BigInteger.valueOf(100))
        val b = XL1Amount.fromAtto(BigInteger.valueOf(200))
        assertEquals(BigInteger.valueOf(300), (a + b).atto.value)
    }

    @Test
    fun `subtraction works`() {
        val a = XL1Amount.fromAtto(BigInteger.valueOf(300))
        val b = XL1Amount.fromAtto(BigInteger.valueOf(100))
        assertEquals(BigInteger.valueOf(200), (a - b).atto.value)
    }

    @Test
    fun `comparison works`() {
        val a = XL1Amount.fromAtto(BigInteger.valueOf(100))
        val b = XL1Amount.fromAtto(BigInteger.valueOf(200))
        assert(a < b)
    }

    @Test
    fun `fromFemto converts correctly`() {
        val amount = XL1Amount.fromFemto(FemtoXL1(BigInteger.ONE))
        assertEquals(BigInteger.TEN.pow(3), amount.atto.value)
    }

    @Test
    fun `fromMilli converts correctly`() {
        val amount = XL1Amount.fromMilli(MilliXL1(BigInteger.ONE))
        assertEquals(BigInteger.TEN.pow(15), amount.atto.value)
    }

    @Test
    fun `clamps to max on overflow`() {
        val hugeValue = AttoXL1.MAX_VALUE + BigInteger.TEN
        val amount = XL1Amount.fromAtto(hugeValue)
        assertEquals(AttoXL1.MAX_VALUE, amount.atto.value)
    }

    @Test
    fun `clamps to zero on negative`() {
        val amount = XL1Amount.fromAtto(BigInteger.valueOf(-100))
        assertEquals(BigInteger.ZERO, amount.atto.value)
    }
}
