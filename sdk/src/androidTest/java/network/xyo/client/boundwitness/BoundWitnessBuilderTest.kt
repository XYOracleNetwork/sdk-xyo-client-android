package network.xyo.client.boundwitness

import MemoryPreviousHashStore
import boundWitnessSequenceTestCases
import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.account.Wallet
import network.xyo.client.lib.JsonSerializable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalStdlibApi::class)
class BoundWitnessBuilderTest {

    @BeforeEach
    fun setUp() {
        Account.previousHashStore = MemoryPreviousHashStore()
    }

    @Test
    fun build_returns_expected_hash() {
        runBlocking {
            for (testCase in boundWitnessSequenceTestCases) {
                // Create accounts
                val signers = mutableListOf<network.xyo.client.account.model.Account>()
                for ((i, mnemonic) in testCase.mnemonics.withIndex()) {
                    val path = testCase.paths[i]
                    val account = try {
                        Wallet.fromMnemonic(mnemonic, path)
                    } catch (e: Exception) {
                        null
                    }
                    if (account != null) {
                        signers.add(account)
                    } else {
                        fail("Error creating account from mnemonic")
                    }
                }

                assertEquals(
                    testCase.addresses.size,
                    signers.size,
                    "Incorrect number of accounts created."
                )

                val expectedAddresses = testCase.addresses.map { it.lowercase() }
                val actualAddresses = signers.map { it.address.toHexString().lowercase() }
                assertEquals(
                    expectedAddresses,
                    actualAddresses,
                    "Incorrect addresses when creating accounts."
                )

                // Ensure correct initial account state (previous hashes)
                for ((i, expectedPreviousHash) in testCase.previousHashes.withIndex()) {
                    val signer = signers[i]
                    val actualPreviousHash = signer.previousHash?.toHexString()
                    assertEquals(
                        expectedPreviousHash,
                        actualPreviousHash,
                        "Incorrect previous hash for account at index $i"
                    )
                }

                // Build the Bound Witness
                val builder = BoundWitnessBuilder()
                    .signers(signers)
                    .payloads(testCase.payloads)

                val (bw, _) = builder.build()

                // Verify payload hashes
                for ((i, expectedPayloadHash) in testCase.payloadHashes.withIndex()) {
                    val actualPayloadHash = bw.payload_hashes.getOrNull(i)
                    assertNotNull(actualPayloadHash, "Payload hash at index $i should not be null.")
                    assertEquals(
                        expectedPayloadHash,
                        actualPayloadHash,
                        "Incorrect payload hash in BW at index $i"
                    )
                }

                // Verify payload schemas
                for ((i, payload) in testCase.payloads.withIndex()) {
                    val actualSchema = bw.payload_schemas.getOrNull(i)
                    assertNotNull(actualSchema, "Payload schema at index $i should not be null.")
                    assertEquals(
                        payload.schema,
                        actualSchema,
                        "Incorrect payload schema in BW at index $i"
                    )
                }

                // Verify BW data hash matches expected (Yellow Paper Section 8.3)
                val dataHashHex = JsonSerializable.bytesToHex(bw.dataHash())
                assertEquals(
                    testCase.dataHash,
                    dataHashHex,
                    "Incorrect data hash in BW"
                )

                // Ensure correct ending account state:
                // After signing, each account's previousHash should be the BW's dataHash
                for (signer in signers) {
                    assertEquals(
                        dataHashHex,
                        signer.previousHash?.toHexString(),
                        "Incorrect previous hash for account ${signer.address.toHexString()}"
                    )
                }
            }
        }
    }
}
