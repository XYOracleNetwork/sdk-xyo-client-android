package network.xyo.client

import com.squareup.moshi.JsonClass
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.lib.JsonSerializable.Companion.MetaExclusion
import network.xyo.client.payload.Payload
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Test vectors from the XYO Yellow Paper (Sections 8.2 and 8.4).
 * These verify canonical serialization and hashing match the reference TypeScript implementation.
 */
class YellowPaperTestVectors {

    // Section 8.4 - Canonical Serialization Verification
    @Test
    fun `payload hash - salt 0`() {
        val json = """{"salt":"0","schema":"network.xyo.id"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("ada56ff753c0c9b2ce5e1f823eda9ac53501db2843d8883d6cf6869c18ef7f65", hash)
    }

    @Test
    fun `payload hash - salt 1`() {
        val json = """{"salt":"1","schema":"network.xyo.id"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("3a3b8deca568ff820b0b7c8714fbdf82b40fb54f4b15aca8745e06b81291558e", hash)
    }

    @Test
    fun `payload hash - salt 2`() {
        val json = """{"salt":"2","schema":"network.xyo.id"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("1a40207fab71fc184e88557d5bee6196cbbb49f11f73cda85000555a628a8f0a", hash)
    }

    @Test
    fun `payload hash - salt 3`() {
        val json = """{"salt":"3","schema":"network.xyo.id"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("c4bce9b4d3239fcc9a248251d1bef1ba7677e3c0c2c43ce909a6668885b519e6", hash)
    }

    @Test
    fun `payload hash - salt 4`() {
        val json = """{"salt":"4","schema":"network.xyo.id"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("59c0374dd801ae64ddddba27320ca028d7bd4b3d460f6674c7da1b4aa9c956d6", hash)
    }

    @Test
    fun `payload hash - salt 5`() {
        val json = """{"salt":"5","schema":"network.xyo.id"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("5d9b8e84bc824280fcbb6290904c2edbb401d626ad9789717c0a23d1cab937b0", hash)
    }

    // Section 8.5 - BoundWitness DataHash Verification (Test Case 1)
    @Test
    fun `bw dataHash - single signer null previous hash`() {
        val json = """{"addresses":["25524ca99764d76ca27604bb9727f6e2f27c4533"],"payload_hashes":["ada56ff753c0c9b2ce5e1f823eda9ac53501db2843d8883d6cf6869c18ef7f65"],"payload_schemas":["network.xyo.id"],"previous_hashes":[null],"schema":"network.xyo.boundwitness"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("750113b9826ba94b622667b06cd8467f1330837581c28907c16160fec20d0a4b", hash)
    }

    // Section 8.2.1 - Location Payload hash/dataHash
    @Test
    fun `location payload dataHash`() {
        val json = """{"currentLocation":{"coords":{"accuracy":5,"altitude":15,"altitudeAccuracy":15,"heading":90,"latitude":37.7749,"longitude":-122.4194,"speed":2.5},"timestamp":1609459200000},"schema":"network.xyo.location.current"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("0c1f0c80481b0f391a677eab542a594a192081325b6416acc3dc99db23355ee2", hash)
    }

    @Test
    fun `location payload hash includes client meta`() {
        // hash() keeps $ fields but strips _ fields. For this test there are no _ fields,
        // so the full JSON with $meta_field is used.
        val json = """{"${'$'}meta_field":"yo","currentLocation":{"coords":{"accuracy":5,"altitude":15,"altitudeAccuracy":15,"heading":90,"latitude":37.7749,"longitude":-122.4194,"speed":2.5},"timestamp":1609459200000},"schema":"network.xyo.location.current"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("d1685b23bbc87c0260620fa6ff3581ffd48574bd326cb472425d4db787af487f", hash)
    }

    // Section 8.2.2 - BoundWitness Payload dataHash
    @Test
    fun `bw payload dataHash with client meta excluded`() {
        // dataHash excludes both $ and _ prefix fields. The data fields only are:
        val json = """{"addresses":["e3b3bb3cdc49e13f9ac5f48d52915368de43afec"],"payload_hashes":["c915c56dd93b5e0db509d1a63ca540cfb211e11f03039b05e19712267bb8b6db"],"payload_schemas":["network.xyo.test"],"previous_hashes":[null],"schema":"network.xyo.boundwitness"}"""
        val hash = JsonSerializable.bytesToHex(JsonSerializable.sha256(json))
        assertEquals("6f731b3956114fd0d18820dbbe1116f9e36dc8d803b0bb049302f7109037468f", hash)
    }

    // Verify that the field exclusion logic produces correct canonical forms
    @Test
    fun `STORAGE_META exclusion keeps dollar fields strips underscore fields`() {
        val json = """{"_hash":"abc","${'$'}meta":"yo","data":"value","schema":"test"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.STORAGE_META)
        assertEquals("""{"${'$'}meta":"yo","data":"value","schema":"test"}""", sorted)
    }

    @Test
    fun `ALL_META exclusion strips both dollar and underscore fields`() {
        val json = """{"_hash":"abc","${'$'}meta":"yo","data":"value","schema":"test"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.ALL_META)
        assertEquals("""{"data":"value","schema":"test"}""", sorted)
    }

    @Test
    fun `nested meta-like fields are preserved`() {
        val json = """{"_top":"removed","data":{"_nested":"kept","${'$'}nested":"kept"},"schema":"test"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.ALL_META)
        assert(sorted.contains("_nested"))
        assert(sorted.contains("\$nested"))
        assert(!sorted.contains("\"_top\""))
    }

    // Verify null handling: per JS reference behavior, `null` is a legitimate
    // value and is preserved both as an object field value and inside arrays.
    // Only `undefined` (which JSON cannot represent) is stripped — the JS side
    // strips it implicitly via JSON.stringify, so no Kotlin equivalent is needed.
    @Test
    fun `null object field values are preserved`() {
        val json = """{"schema":"test","timestamp":null,"data":"value"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.NONE)
        assertEquals("""{"data":"value","schema":"test","timestamp":null}""", sorted)
    }

    @Test
    fun `null inside arrays is preserved`() {
        val json = """{"previous_hashes":[null],"schema":"network.xyo.boundwitness"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.NONE)
        assertEquals("""{"previous_hashes":[null],"schema":"network.xyo.boundwitness"}""", sorted)
    }

    @Test
    fun `null inside arrays with mixed values is preserved`() {
        val json = """{"hashes":["abc",null,"def"],"schema":"test"}"""
        val sorted = JsonSerializable.sortJson(json, MetaExclusion.NONE)
        assertEquals("""{"hashes":["abc",null,"def"],"schema":"test"}""", sorted)
    }
}
