package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.block.XL1BlockNumber
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class XL1BlockRangeTest {

    @Test
    fun `creates from valid range`() {
        val range = XL1BlockRange(XL1BlockNumber(0), XL1BlockNumber(100))
        assertEquals(0L, range.start.value)
        assertEquals(100L, range.end.value)
    }

    @Test
    fun `span calculates correctly`() {
        val range = XL1BlockRange(XL1BlockNumber(10), XL1BlockNumber(50))
        assertEquals(40L, range.span)
    }

    @Test
    fun `rejects invalid range where start gt end`() {
        assertThrows<IllegalArgumentException> {
            XL1BlockRange(XL1BlockNumber(100), XL1BlockNumber(10))
        }
    }

    @Test
    fun `allows equal start and end`() {
        val range = XL1BlockRange(XL1BlockNumber(50), XL1BlockNumber(50))
        assertEquals(0L, range.span)
    }

    @Test
    fun `toKey produces correct format`() {
        val range = XL1BlockRange(XL1BlockNumber(10), XL1BlockNumber(20))
        assertEquals("10|20", range.toKey())
    }

    @Test
    fun `fromKey parses valid key`() {
        val range = XL1BlockRange.fromKey("10|20")
        assertNotNull(range)
        assertEquals(10L, range!!.start.value)
        assertEquals(20L, range.end.value)
    }

    @Test
    fun `fromKey returns null for invalid key`() {
        assertNull(XL1BlockRange.fromKey("invalid"))
        assertNull(XL1BlockRange.fromKey("10"))
        assertNull(XL1BlockRange.fromKey(""))
        assertNull(XL1BlockRange.fromKey("abc|def"))
    }

    @Test
    fun `round trip toKey and fromKey`() {
        val original = XL1BlockRange(XL1BlockNumber(100), XL1BlockNumber(500))
        val key = original.toKey()
        val restored = XL1BlockRange.fromKey(key)
        assertEquals(original, restored)
    }
}
