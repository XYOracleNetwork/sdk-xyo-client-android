package network.xyo.client

import network.xyo.client.lib.JsonSerializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class JsonSerializableTest {

    @Test
    fun `sortJson sorts keys alphabetically`() {
        val unsorted = """{"z":"last","a":"first","m":"middle"}"""
        val sorted = JsonSerializable.sortJson(unsorted)
        assertEquals("""{"a":"first","m":"middle","z":"last"}""", sorted)
    }

    @Test
    fun `sortJson handles nested objects`() {
        val unsorted = """{"b":{"z":1,"a":2},"a":1}"""
        val sorted = JsonSerializable.sortJson(unsorted)
        assertEquals("""{"a":1,"b":{"a":2,"z":1}}""", sorted)
    }

    @Test
    fun `sortJson removes meta fields when removeMeta is true`() {
        val json = """{"schema":"test","_hash":"abc","${'$'}signatures":["sig1"],"data":"value"}"""
        val sorted = JsonSerializable.sortJson(json, removeMeta = true)
        assertEquals("""{"data":"value","schema":"test"}""", sorted)
    }

    @Test
    fun `sortJson preserves meta fields when removeMeta is false`() {
        val json = """{"schema":"test","_hash":"abc","data":"value"}"""
        val sorted = JsonSerializable.sortJson(json, removeMeta = false)
        assert(sorted.contains("_hash"))
    }

    @Test
    fun `sha256 produces consistent hash for same input`() {
        val hash1 = JsonSerializable.sha256("hello")
        val hash2 = JsonSerializable.sha256("hello")
        assertEquals(hash1.toList(), hash2.toList())
    }

    @Test
    fun `sha256 produces different hash for different input`() {
        val hash1 = JsonSerializable.sha256("hello")
        val hash2 = JsonSerializable.sha256("world")
        assertNotEquals(hash1.toList(), hash2.toList())
    }

    @Test
    fun `sha256 produces 32-byte hash`() {
        val hash = JsonSerializable.sha256("test")
        assertEquals(32, hash.size)
    }

    @Test
    fun `bytesToHex produces correct hex string`() {
        val bytes = byteArrayOf(0x0A, 0xFF.toByte(), 0x00, 0x10)
        val hex = JsonSerializable.bytesToHex(bytes)
        assertEquals("0aff0010", hex)
    }

    @Test
    fun `bytesToHex produces lowercase hex`() {
        val bytes = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte())
        val hex = JsonSerializable.bytesToHex(bytes)
        assertEquals("abcdef", hex)
    }
}
