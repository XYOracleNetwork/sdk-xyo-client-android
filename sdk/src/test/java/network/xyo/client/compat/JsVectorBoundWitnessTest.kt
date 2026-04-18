package network.xyo.client.compat

import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.lib.JsonSerializable.Companion.MetaExclusion
import network.xyo.client.lib.hexStringToByteArray
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Verifies the full BoundWitness cryptographic contract is cross-SDK compatible:
 *
 * 1. Each payload's canonical hash (`dataHash`) matches the JS-produced hash.
 * 2. The BoundWitness `dataHash` — the hash over
 *    `{addresses, payload_hashes, payload_schemas, previous_hashes, schema}` —
 *    matches the JS-produced `dataHash`.
 * 3. Each signer's ECDSA signature over that `dataHash` matches the JS-produced
 *    signature.
 *
 * This test deliberately reconstructs the BW dataHashable form from raw fields
 * rather than driving the Kotlin BoundWitnessBuilder end-to-end — the builder
 * is tested against the Android-instrumentation BoundWitnessBuilderTest. Here
 * we target the cryptographic primitives.
 */
@OptIn(ExperimentalStdlibApi::class)
class JsVectorBoundWitnessTest {

    @TestFactory
    fun `Kotlin BoundWitness primitives match JS vectors`(): List<DynamicTest> {
        val tests = mutableListOf<DynamicTest>()
        val bws = JsCompatVectors.boundWitnesses
        for (i in 0 until bws.length()) {
            val bw = bws.getJSONObject(i)
            val id = bw.getString("id")
            val payloadsRaw = bw.getJSONArray("payloads_raw_json")
            val expectedPayloadHashes = bw.getJSONArray("payload_hashes")
            val expectedPayloadSchemas = bw.getJSONArray("payload_schemas")
            val expectedAddresses = bw.getJSONArray("addresses")
            val expectedPrevious = bw.getJSONArray("previous_hashes")
            val expectedDataHash = bw.getString("data_hash")
            val expectedSignatures = bw.getJSONArray("signatures")
            val signerKeys = bw.getJSONArray("signer_private_keys")

            tests += DynamicTest.dynamicTest("[$id] payload hashes") {
                for (p in 0 until payloadsRaw.length()) {
                    val canonical = JsonSerializable.sortJson(payloadsRaw.getString(p), MetaExclusion.ALL_META)
                    val actual = JsonSerializable.bytesToHex(JsonSerializable.sha256(canonical))
                    assertEquals(expectedPayloadHashes.getString(p), actual, "payload $p")
                }
            }

            tests += DynamicTest.dynamicTest("[$id] addresses derive from signer private keys") {
                for (s in 0 until signerKeys.length()) {
                    val account = Account.fromPrivateKey(hexStringToByteArray(signerKeys.getString(s)))
                    assertEquals(expectedAddresses.getString(s), account.address.toHexString(), "signer $s")
                }
            }

            tests += DynamicTest.dynamicTest("[$id] dataHash matches JS") {
                val dataHashableJson = buildDataHashableJson(
                    addresses = expectedAddresses,
                    payloadHashes = expectedPayloadHashes,
                    payloadSchemas = expectedPayloadSchemas,
                    previousHashes = expectedPrevious,
                )
                val canonical = JsonSerializable.sortJson(dataHashableJson, MetaExclusion.ALL_META)
                val actual = JsonSerializable.bytesToHex(JsonSerializable.sha256(canonical))
                assertEquals(expectedDataHash, actual)
            }

            tests += DynamicTest.dynamicTest("[$id] signers produce JS-matching signatures") {
                runBlocking {
                    val hashBytes = hexStringToByteArray(expectedDataHash)
                    for (s in 0 until signerKeys.length()) {
                        val account = Account.fromPrivateKey(hexStringToByteArray(signerKeys.getString(s)))
                        val actualSig = account.sign(hashBytes).toHexString()
                        assertEquals(expectedSignatures.getString(s), actualSig, "signer $s")
                    }
                }
            }
        }
        return tests
    }

    /**
     * Build the JSON for the BoundWitness dataHashable fields. Null entries in
     * previous_hashes are preserved as JSON null (not stripped) — matches the JS
     * builder's output where a fresh signer has a null previousHash.
     */
    private fun buildDataHashableJson(
        addresses: JSONArray,
        payloadHashes: JSONArray,
        payloadSchemas: JSONArray,
        previousHashes: JSONArray,
    ): String {
        val obj = JSONObject()
        obj.put("addresses", addresses)
        obj.put("payload_hashes", payloadHashes)
        obj.put("payload_schemas", payloadSchemas)
        obj.put("previous_hashes", previousHashes)
        obj.put("schema", "network.xyo.boundwitness")
        return obj.toString()
    }
}
