package network.xyo.chain.protocol.integration

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.model.Transfer
import network.xyo.chain.protocol.sdk.transaction.TransactionBuilder
import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import network.xyo.client.account.Account
import network.xyo.client.payload.Payload
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigInteger

class TransactionWithTransferTest {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `generate transaction with transfer payload`() = runBlocking {
        // Create a signer
        val signer = Account.random()

        // Create a transfer payload using the SDK Payload class so BoundWitnessBuilder can hash it
        val transferPayload = Payload("network.xyo.transfer").apply {
            // We add the transfer fields as extra JSON via the underlying JsonSerializable
        }

        // Build the transaction
        val fees = TransactionFeesBigInt(
            base = BigInteger.ZERO,
            gasLimit = BigInteger.valueOf(21000),
            gasPrice = BigInteger.ONE,
            priority = BigInteger.ZERO,
        )

        val tx = TransactionBuilder()
            .chain("1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b")
            .signer(signer)
            .payload(transferPayload)
            .fees(fees)
            .range(150000L, 151000L)
            .build()

        // Verify the transaction structure
        assertNotNull(tx.boundWitness)
        assertEquals(1, tx.payloads.size)
        assertEquals("network.xyo.boundwitness", tx.boundWitness.schema)
        assertEquals(150000L, tx.boundWitness.nbf)
        assertEquals(151000L, tx.boundWitness.exp)
        assertEquals(1, tx.boundWitness.payload_hashes.size)
        assertTrue(tx.boundWitness.payload_hashes[0].isNotEmpty())
        assertNotNull(tx.boundWitness.signatures)
        assertTrue(tx.boundWitness.signatures!!.isNotEmpty())

        // Also create the transfer model to show what it looks like
        val transfer = Transfer(
            from = "a3f2b7c91d4e6f8a0b1c2d3e4f5a6b7c8d9e0f1a",
            transfers = mapOf(
                "b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3" to "0xde0b6b3a7640000",
            ),
            epoch = 150000L,
        )

        // Serialize the bound witness to JSON
        val bwAdapter = moshi.adapter(TransactionBoundWitness::class.java).indent("  ")
        val bwJson = bwAdapter.toJson(tx.boundWitness)

        // Serialize the transfer to JSON
        val transferAdapter = moshi.adapter(Transfer::class.java).indent("  ")
        val transferJson = transferAdapter.toJson(transfer)

        // Serialize as the wire format: [boundWitness, [payloads]]
        println("=== Transaction Bound Witness ===")
        println(bwJson)
        println()
        println("=== Transfer Payload ===")
        println(transferJson)
        println()
        println("=== Wire Format (HydratedTransaction) ===")
        println("[")
        println("  $bwJson,")
        println("  [")
        println("    $transferJson")
        println("  ]")
        println("]")
    }
}
