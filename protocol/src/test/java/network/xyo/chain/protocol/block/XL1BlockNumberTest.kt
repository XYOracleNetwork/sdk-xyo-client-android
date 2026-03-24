package network.xyo.chain.protocol.block

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class XL1BlockNumberTest {

    @Test
    fun `creates from valid value`() {
        val block = XL1BlockNumber(100L)
        assertEquals(100L, block.value)
    }

    @Test
    fun `ZERO is zero`() {
        assertEquals(0L, XL1BlockNumber.ZERO.value)
    }

    @Test
    fun `rejects negative value`() {
        assertThrows<IllegalArgumentException> {
            XL1BlockNumber(-1L)
        }
    }

    @Test
    fun `addition with Long works`() {
        val block = XL1BlockNumber(100L)
        val result = block + 50L
        assertEquals(150L, result.value)
    }

    @Test
    fun `subtraction between blocks returns Long`() {
        val a = XL1BlockNumber(150L)
        val b = XL1BlockNumber(100L)
        assertEquals(50L, a - b)
    }

    @Test
    fun `comparison works correctly`() {
        val a = XL1BlockNumber(100L)
        val b = XL1BlockNumber(200L)
        assert(a < b)
        assert(b > a)
        assertEquals(0, a.compareTo(XL1BlockNumber(100L)))
    }

    @Test
    fun `of(Long) factory works`() {
        val block = XL1BlockNumber.of(42L)
        assertEquals(42L, block.value)
    }

    @Test
    fun `of(Int) factory works`() {
        val block = XL1BlockNumber.of(42)
        assertEquals(42L, block.value)
    }

    @Test
    fun `ofOrNull returns null for negative`() {
        assertNull(XL1BlockNumber.ofOrNull(-1L))
    }

    @Test
    fun `ofOrNull returns value for valid`() {
        assertNotNull(XL1BlockNumber.ofOrNull(100L))
    }

    @Test
    fun `toString returns decimal string`() {
        assertEquals("42", XL1BlockNumber(42L).toString())
    }
}
