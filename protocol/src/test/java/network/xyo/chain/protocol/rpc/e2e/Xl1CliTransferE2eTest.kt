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
    fun `transfers from the genesis reward address to another address`() = runBlocking {
        val sender = E2eRpcSupport.genesisRewardWallet()
        val senderAddress = sender.address.toHexString()
        val recipient = Account.random()
        val recipientAddress = recipient.address.toHexString()
        val amount = AttoXL1.of(BigInteger("1000000000000000000"))

        val initialBalances = accountBalanceViewer.qualifiedAccountBalances(
            listOf(senderAddress, recipientAddress),
            network.xyo.chain.protocol.model.AccountBalanceConfig(),
        )
        val initialSenderBalance = initialBalances.data[senderAddress] ?: AttoXL1.ZERO
        val initialRecipientBalance = initialBalances.data[recipientAddress] ?: AttoXL1.ZERO
        val maxFeeExposure = AttoXL1.of(
            DefaultTransactionFees.default.base +
                DefaultTransactionFees.default.priority +
                DefaultTransactionFees.default.gasLimit,
        )
        val requiredSenderBalance = amount + maxFeeExposure

        assertTrue(
            initialSenderBalance >= requiredSenderBalance,
            "sender must cover transfer amount ($amount) plus max fee exposure ($maxFeeExposure); actual=$initialSenderBalance",
        )
        assertEquals(AttoXL1.ZERO, initialRecipientBalance, "fresh recipient should start at zero")

        val head = blockViewer.currentBlock()
        val transaction = TransactionBuilder()
            .chain(head.boundWitness.chain)
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
            .range(head.boundWitness.block, head.boundWitness.block + 1000)
            .build()

        val signedTransaction = E2eRpcSupport.toSignedTransaction(transaction)
        mempoolRunner.submitTransactions(listOf(signedTransaction)).single()

        val finalBalances = E2eRpcSupport.awaitBalanceAtLeast(
            viewer = accountBalanceViewer,
            address = recipientAddress,
            expectedMinimum = initialRecipientBalance + amount,
        )

        assertEquals(initialRecipientBalance + amount, finalBalances.data[recipientAddress])
    }
}
