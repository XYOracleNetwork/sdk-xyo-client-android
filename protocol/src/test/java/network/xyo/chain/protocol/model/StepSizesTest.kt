package network.xyo.chain.protocol.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StepSizesTest {

    @Test
    fun `has 9 step sizes`() {
        assertEquals(9, StepSizes.values.size)
    }

    @Test
    fun `step sizes match expected values`() {
        assertEquals(7L, StepSizes.values[0])
        assertEquals(31L, StepSizes.values[1])
        assertEquals(211L, StepSizes.values[2])
        assertEquals(2_311L, StepSizes.values[3])
        assertEquals(30_031L, StepSizes.values[4])
        assertEquals(510_511L, StepSizes.values[5])
        assertEquals(9_699_691L, StepSizes.values[6])
        assertEquals(223_092_871L, StepSizes.values[7])
        assertEquals(6_469_693_231L, StepSizes.values[8])
    }

    @Test
    fun `stepSize returns correct value for valid index`() {
        assertEquals(7L, StepSizes.stepSize(0))
        assertEquals(6_469_693_231L, StepSizes.stepSize(8))
    }

    @Test
    fun `stepSize throws for invalid index`() {
        assertThrows<IllegalArgumentException> { StepSizes.stepSize(-1) }
        assertThrows<IllegalArgumentException> { StepSizes.stepSize(9) }
    }

    @Test
    fun `step sizes are monotonically increasing`() {
        for (i in 1 until StepSizes.values.size) {
            assert(StepSizes.values[i] > StepSizes.values[i - 1])
        }
    }

    @Test
    fun `reward fractions has 7 entries`() {
        assertEquals(7, StepSizes.rewardFractions.size)
    }

    @Test
    fun `first three reward fractions are zero`() {
        for (i in 0..2) {
            assertEquals(java.math.BigInteger.ZERO, StepSizes.rewardFractions[i].first)
        }
    }
}
