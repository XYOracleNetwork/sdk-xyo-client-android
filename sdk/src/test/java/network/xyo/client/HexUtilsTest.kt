package network.xyo.client

import network.xyo.client.lib.hexStringToByteArray
import network.xyo.client.lib.JsonSerializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HexUtilsTest {

    @Test
    fun `hexStringToByteArray converts valid hex`() {
        val bytes = hexStringToByteArray("0aff0010")
        assertEquals(4, bytes.size)
        assertEquals(0x0A, bytes[0].toInt() and 0xFF)
        assertEquals(0xFF, bytes[1].toInt() and 0xFF)
        assertEquals(0x00, bytes[2].toInt() and 0xFF)
        assertEquals(0x10, bytes[3].toInt() and 0xFF)
    }

    @Test
    fun `hex roundtrip preserves data`() {
        val original = byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte())
        val hex = JsonSerializable.bytesToHex(original)
        val restored = hexStringToByteArray(hex)
        assertEquals(original.toList(), restored.toList())
    }

    @Test
    fun `empty hex string produces empty byte array`() {
        val bytes = hexStringToByteArray("")
        assertEquals(0, bytes.size)
    }
}
