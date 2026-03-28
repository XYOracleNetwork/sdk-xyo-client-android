package network.xyo.client

import network.xyo.client.lib.JsonSerializable
import network.xyo.client.lib.JsonSerializable.Companion.MetaExclusion
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
    fun `sortJson ALL_META removes both _ and $ fields`() {
        val json = """{"schema":"test","_hash":"abc","${'$'}signatures":["sig1"],"data":"value"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.ALL_META)
        assertEquals("""{"data":"value","schema":"test"}""", sorted)
    }

    @Test
    fun `sortJson STORAGE_META removes only _ fields, keeps $ fields`() {
        val json = """{"schema":"test","_hash":"abc","${'$'}signatures":["sig1"],"data":"value"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.STORAGE_META)
        assertEquals("""{"${'$'}signatures":["sig1"],"data":"value","schema":"test"}""", sorted)
    }

    @Test
    fun `sortJson NONE preserves all fields`() {
        val json = """{"schema":"test","_hash":"abc","data":"value"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.NONE)
        assert(sorted.contains("_hash"))
        assert(sorted.contains("data"))
        assert(sorted.contains("schema"))
    }

    @Test
    fun `sortJson removeMeta=true is equivalent to ALL_META`() {
        val json = """{"schema":"test","_hash":"abc","${'$'}sig":["s"],"data":"value"}"""
        val withBool = JsonSerializable.sortJson(json, removeMeta = true)
        val withEnum = JsonSerializable.sortJson(json, MetaExclusion.ALL_META)
        assertEquals(withBool, withEnum)
    }

    @Test
    fun `sortJson removeMeta=false is equivalent to NONE`() {
        val json = """{"schema":"test","_hash":"abc","data":"value"}"""
        val withBool = JsonSerializable.sortJson(json, removeMeta = false)
        val withEnum = JsonSerializable.sortJson(json, MetaExclusion.NONE)
        assertEquals(withBool, withEnum)
    }

    @Test
    fun `field exclusion applies only at top level`() {
        val json = """{"data":{"_nested":"value","${'$'}nested":"meta"},"_top":"removed","schema":"test"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.ALL_META)
        assert(!sorted.contains("\"_top\""))
        assert(sorted.contains("_nested"))
        assert(sorted.contains("\$nested"))
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

    // Yellow Paper Section 8.4 - Canonical Serialization Verification
    @Test
    fun `yellow paper payload hash test vector`() {
        val json = """{"salt":"0","schema":"network.xyo.id"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.ALL_META)
        assertEquals("""{"salt":"0","schema":"network.xyo.id"}""", sorted)
        val hash = JsonSerializable.sha256(sorted)
        val hex = JsonSerializable.bytesToHex(hash)
        assertEquals("ada56ff753c0c9b2ce5e1f823eda9ac53501db2843d8883d6cf6869c18ef7f65", hex)
    }
}
