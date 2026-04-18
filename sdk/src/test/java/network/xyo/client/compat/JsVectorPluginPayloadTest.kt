package network.xyo.client.compat

import network.xyo.client.lib.JsonSerializable
import network.xyo.client.payload.Payload
import network.xyo.client.payload.types.AddressPayload
import network.xyo.client.payload.types.ConfigPayload
import network.xyo.client.payload.types.DomainPayload
import network.xyo.client.payload.types.IdPayload
import network.xyo.client.payload.types.SchemaPayload
import network.xyo.client.payload.types.ValuePayload
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * End-to-end parity check for each Kotlin payload subclass under
 * `network.xyo.client.payload.types`. Each vector in the shared file that is
 * tagged with a `plugin-*` id names a payload subclass; this test constructs
 * the Kotlin equivalent with the same field values and asserts that
 * `dataHash()` — which runs the full Moshi → canonicalize → SHA-256 pipeline
 * — produces the JS-side expected hash.
 *
 * Complements [JsVectorPayloadHashTest], which tests the canonicalizer alone
 * over raw JSON. Here we additionally verify that Moshi's serialization of
 * each Kotlin data class emits exactly the fields the JS SDK does.
 */
class JsVectorPluginPayloadTest {

    @TestFactory
    fun `Kotlin plugin-typed payloads hash match JS vectors`(): List<DynamicTest> {
        val byId = mutableMapOf<String, Pair<String, String>>() // id -> (dataHash, hash)
        val vectors = JsCompatVectors.payloadHashes
        for (i in 0 until vectors.length()) {
            val v = vectors.getJSONObject(i)
            byId[v.getString("id")] = v.getString("data_hash") to v.getString("hash")
        }

        val cases: List<Triple<String, Payload, String>> = listOf(
            Triple(
                "plugin-address",
                AddressPayload(address = "1234567890abcdef1234567890abcdef12345678", name = "primary-signer"),
                "AddressPayload",
            ),
            Triple(
                "plugin-config",
                ConfigPayload(config = mapOf("timeout" to 30, "retries" to 3, "enabled" to true, "label" to "test-config")),
                "ConfigPayload",
            ),
            Triple(
                "plugin-domain",
                DomainPayload(domain = "xyo.network", aliases = mapOf("primary" to "xyo.network", "cdn" to "cdn.xyo.network")),
                "DomainPayload",
            ),
            Triple("plugin-id", IdPayload(salt = "cross-sdk-id-vector"), "IdPayload"),
            Triple(
                "plugin-schema",
                SchemaPayload(definition = mapOf("title" to "test", "version" to 1, "fields" to listOf("a", "b", "c"))),
                "SchemaPayload",
            ),
            Triple("plugin-value-number", ValuePayload(value = 42), "ValuePayload(number)"),
            Triple("plugin-value-string", ValuePayload(value = "hello-value-plugin"), "ValuePayload(string)"),
            Triple(
                "plugin-value-object",
                ValuePayload(value = mapOf("key" to "nested", "n" to 99)),
                "ValuePayload(object)",
            ),
        )

        return cases.flatMap { (id, payload, label) ->
            val (expectedDataHash, expectedHash) = byId[id]
                ?: error("No vector for id=$id (regenerate jsCompatVectors.json?)")
            listOf(
                DynamicTest.dynamicTest("[$label] dataHash") {
                    assertEquals(expectedDataHash, JsonSerializable.bytesToHex(payload.dataHash()))
                },
                DynamicTest.dynamicTest("[$label] hash") {
                    assertEquals(expectedHash, JsonSerializable.bytesToHex(payload.hash()))
                },
            )
        }
    }
}
