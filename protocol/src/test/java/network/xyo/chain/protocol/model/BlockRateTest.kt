package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.block.XL1BlockNumber
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BlockRateTest {

    @Test
    fun `creates BlockRate with all fields`() {
        val rate = BlockRate(
            range = XL1BlockRange(XL1BlockNumber(0), XL1BlockNumber(100)),
            rate = 2.5,
            timeUnit = TimeUnit.seconds,
            span = 100L,
            timeDifference = 40.0,
            timePerBlock = 0.4,
        )
        assertEquals(2.5, rate.rate)
        assertEquals(TimeUnit.seconds, rate.timeUnit)
        assertEquals(100L, rate.span)
    }

    @Test
    fun `TimeDurations defaults to zero`() {
        val td = TimeDurations()
        assertEquals(0.0, td.millis)
        assertEquals(0.0, td.seconds)
        assertEquals(0.0, td.minutes)
        assertEquals(0.0, td.hours)
        assertEquals(0.0, td.days)
        assertEquals(0.0, td.weeks)
    }

    @Test
    fun `TimeConfig all fields optional`() {
        val tc = TimeConfig()
        assertEquals(null, tc.minutes)
        assertEquals(null, tc.hours)
        assertEquals(null, tc.days)
    }

    @Test
    fun `TimeConfig with one field`() {
        val tc = TimeConfig(hours = 1.0)
        assertEquals(1.0, tc.hours)
        assertEquals(null, tc.minutes)
    }

    @Test
    fun `TimeUnit enum has all values`() {
        val values = TimeUnit.entries
        assertEquals(6, values.size)
        assertEquals(TimeUnit.millis, TimeUnit.valueOf("millis"))
        assertEquals(TimeUnit.weeks, TimeUnit.valueOf("weeks"))
    }
}
