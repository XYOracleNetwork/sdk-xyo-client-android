package network.xyo.chain.protocol.rpc.e2e

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.model.DefaultTransactionFees
import network.xyo.chain.protocol.payload.TransferPayload
import network.xyo.chain.protocol.rpc.runner.JsonRpcMempoolRunner
import network.xyo.chain.protocol.rpc.viewer.JsonRpcAccountBalanceViewer
import network.xyo.chain.protocol.rpc.viewer.JsonRpcBlockViewer
import network.xyo.chain.protocol.sdk.transaction.TransactionBuilder
import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.client.account.Account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.math.BigInteger

@EnabledIfEnvironmentVariable(named = "XL1_E2E_RPC_URL", matches = ".+")
class Xl1CliTransferE2eTest {
    private val transport = E2eRpcSupport.transport()
    private val blockViewer = JsonRpcBlockViewer(transport)
    private val mempoolRunner = JsonRpcMempoolRunner(transport)
    private val accountBalanceViewer = JsonRpcAccountBalanceViewer(transport)

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `transfers from the genesis reward address across several produced blocks`() = runBlocking {
        val sender = E2eRpcSupport.genesisRewardWallet()
        val senderAddress = sender.address.toHexString()
        val recipients = List(3) { Account.random() }
        val amount = AttoXL1.of(BigInteger("1000000000000000000"))
        val initialHead = blockViewer.currentBlock()

        val initialBalances = accountBalanceViewer.qualifiedAccountBalances(
            listOf(senderAddress) + recipients.map { it.address.toHexString() },
            network.xyo.chain.protocol.model.AccountBalanceConfig(),
        )
        val initialSenderBalance = initialBalances.data[senderAddress] ?: AttoXL1.ZERO
        val maxFeeExposure = AttoXL1.of(
            DefaultTransactionFees.default.base +
                DefaultTransactionFees.default.priority +
                DefaultTransactionFees.default.gasLimit,
        )
        val requiredSenderBalance = AttoXL1.of(
            (amount.value + maxFeeExposure.value).multiply(BigInteger.valueOf(recipients.size.toLong())),
        )

        assertTrue(
            initialSenderBalance >= requiredSenderBalance,
            "sender must cover ${recipients.size} transfers plus fees; required=$requiredSenderBalance actual=$initialSenderBalance",
        )
        recipients.forEach { recipient ->
            val recipientAddress = recipient.address.toHexString()
            val initialRecipientBalance = initialBalances.data[recipientAddress] ?: AttoXL1.ZERO
            assertEquals(AttoXL1.ZERO, initialRecipientBalance, "fresh recipient should start at zero")
        }

        var observedBlock = initialHead.boundWitness.block
        recipients.forEach { recipient ->
            val recipientAddress = recipient.address.toHexString()
            val currentHead = blockViewer.currentBlock()
            val transaction = TransactionBuilder()
                .chain(currentHead.boundWitness.chain)
                .from(senderAddress)
                .signer(sender)
                .payload(
                    TransferPayload(
                        from = senderAddress,
                        transfers = mapOf(recipientAddress to amount.toHex().removePrefix("0x")),
                        epoch = System.currentTimeMillis(),
                    ),
                )
                .fees(DefaultTransactionFees.default)
                .range(currentHead.boundWitness.block, currentHead.boundWitness.block + 1000)
                .build()

            val signedTransaction = E2eRpcSupport.toSignedTransaction(transaction)
            mempoolRunner.submitTransactions(listOf(signedTransaction)).single()

            val advancedBlock = E2eRpcSupport.awaitBlockAtLeast(
                viewer = blockViewer,
                expectedMinimum = observedBlock + 1,
            )
            assertTrue(
                advancedBlock > observedBlock,
                "expected a new block after submitting transfer to $recipientAddress",
            )
            observedBlock = advancedBlock

            val finalBalances = E2eRpcSupport.awaitBalanceAtLeast(
                viewer = accountBalanceViewer,
                address = recipientAddress,
                expectedMinimum = amount,
            )

            assertEquals(amount, finalBalances.data[recipientAddress])
        }

        assertTrue(
            observedBlock >= initialHead.boundWitness.block + recipients.size,
            "expected at least ${recipients.size} new blocks after genesis; initial=${initialHead.boundWitness.block} final=$observedBlock",
        )
    }
}
