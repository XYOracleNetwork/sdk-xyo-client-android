package network.xyo.client.compat

import network.xyo.client.lib.JsonSerializable
import network.xyo.client.lib.JsonSerializable.Companion.MetaExclusion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Verifies Kotlin's canonical-JSON serialization and SHA-256 hashing match
 * the JS `ObjectHasher` / `PayloadBuilder` output for a varied corpus of
 * payloads. `raw_json` is the JSON.stringify output from JS — Kotlin
 * canonicalizes it (sorts keys, strips meta) and hashes.
 *
 * - `data_hash` strips both storage (`_`) and client (`$`) meta.
 * - `hash` strips only storage meta; `$` fields are included.
 *
 * Note: intentional `null` values as object field values are deliberately
 * excluded from the corpus — Kotlin strips them at canonicalization while
 * JS preserves them. Resolving that divergence is tracked separately.
 */
class JsVectorPayloadHashTest {

    @TestFactory
    fun `Kotlin canonical hash matches JS vectors`(): List<DynamicTest> {
        val tests = mutableListOf<DynamicTest>()
        val vectors = JsCompatVectors.payloadHashes
        for (i in 0 until vectors.length()) {
            val v = vectors.getJSONObject(i)
            val id = v.getString("id")
            val raw = v.getString("raw_json")
            val expectedDataHash = v.getString("data_hash")
            val expectedHash = v.getString("hash")

            tests += DynamicTest.dynamicTest("dataHash [$id]") {
                val canonical = JsonSerializable.sortJson(raw, MetaExclusion.ALL_META)
                val hex = JsonSerializable.bytesToHex(JsonSerializable.sha256(canonical))
                assertEquals(expectedDataHash, hex)
            }

            tests += DynamicTest.dynamicTest("hash [$id]") {
                val canonical = JsonSerializable.sortJson(raw, MetaExclusion.STORAGE_META)
                val hex = JsonSerializable.bytesToHex(JsonSerializable.sha256(canonical))
                assertEquals(expectedHash, hex)
            }
        }
        return tests
    }
}
