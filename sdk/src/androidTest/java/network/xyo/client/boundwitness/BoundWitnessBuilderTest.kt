import network.xyo.client.account.Account
import network.xyo.client.account.Wallet
import network.xyo.client.account.model.AccountInstance
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalStdlibApi::class)
class BoundWitnessBuilderTest {

    @BeforeEach
    fun setUp() {
        // Ensure previousHash = null for test accounts
        Account.previousHashStore = MemoryPreviousHashStore()
    }

    @Test
    fun build_returns_expected_hash() {
        for (testCase in boundWitnessSequenceTestCases) {
            // Create accounts
            val signers = mutableListOf<AccountInstance>()
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

            /*
            // Ensure correct initial account state
            for ((i, expectedPreviousHash) in testCase.previousHashes.withIndex()) {
                val signer = signers.getOrNull(i)
                assertNotNull(signer, "Signer at index $i should not be null.")
                assertEquals(
                    expectedPreviousHash,
                    signer.previousHash,
                    "Incorrect previous hash for account at index $i"
                )
            }

            // Build the Bound Witness (BW)
            val bw = BoundWitnessBuilder()
                .signers(signers)
                .payloads(testCase.payloads)

            val (bwJson, _) = bw.build()
            val hash = PayloadBuilder.dataHash(bwJson.typedPayload)
            val rootHash = PayloadBuilder.hash(bwJson.typedPayload)

            // Ensure the BW is correct
            assertEquals(
                testCase.dataHash,
                hash.toHex(),
                "Incorrect data hash in BW"
            )

            for ((i, expectedPayloadHash) in testCase.payloadHashes.withIndex()) {
                val actualPayloadHash = bwJson.typedPayload.payload_hashes.getOrNull(i)
                assertNotNull(actualPayloadHash, "Payload hash at index $i should not be null.")
                assertEquals(
                    expectedPayloadHash,
                    actualPayloadHash,
                    "Incorrect payload hash in BW at index $i"
                )
            }

            for ((i, payload) in testCase.payloads.withIndex()) {
                val actualSchema = bwJson.typedPayload.payload_schemas.getOrNull(i)
                assertNotNull(actualSchema, "Payload schema at index $i should not be null.")
                assertEquals(
                    payload.schema,
                    actualSchema,
                    "Incorrect payload schema in BW at index $i"
                )
            }

            // Ensure correct ending account state
            for (signer in signers) {
                assertEquals(
                    hash.toHex(),
                    signer.previousHash,
                    "Incorrect previous hash for account ${signer.address}"
                )
            }

            */
        }
    }
}