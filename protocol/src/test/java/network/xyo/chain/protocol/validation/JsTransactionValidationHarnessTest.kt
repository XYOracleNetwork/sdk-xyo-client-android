package network.xyo.chain.protocol.validation

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.model.DefaultTransactionFees
import network.xyo.chain.protocol.payload.TransferPayload
import network.xyo.chain.protocol.sdk.transaction.TransactionBuilder
import network.xyo.client.account.Account
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files

class JsTransactionValidationHarnessTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `generated transfer transaction passes authoritative js validation`() = runBlocking {
        val xl1Repo = JsTransactionValidationHarnessSupport.defaultXl1Repo()
        assumeTrue(Files.exists(xl1Repo), "XL1 protocol repo not available: $xl1Repo")
        assumeTrue(
            Files.exists(xl1Repo.resolve("packages/protocol/packages/validation/dist/neutral/index.mjs")),
            "XL1 validation dist not built in $xl1Repo",
        )

        val signer = Account.random()
        val recipient = Account.random()
        val senderAddress = signer.address.toHexString()
        val recipientAddress = recipient.address.toHexString()

        val transaction = TransactionBuilder()
            .chain("c5fe2e6f6841cbab12d8c0618be2df8c6156cc44")
            .from(senderAddress)
            .signer(signer)
            .payload(
                TransferPayload(
                    from = senderAddress,
                    transfers = mapOf(recipientAddress to "de0b6b3a7640000"),
                    epoch = System.currentTimeMillis(),
                ),
            )
            .fees(DefaultTransactionFees.default)
            .range(100L, 200L)
            .build()

        val signed = network.xyo.chain.protocol.rpc.e2e.E2eRpcSupport.toSignedTransaction(transaction)
        val result = JsTransactionValidationHarnessSupport.runValidation(signed, xl1Repo)

        assertTrue(result.errors.isEmpty(), "Expected no JS validation errors, got: ${result.errors}")
    }
}
