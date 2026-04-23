package network.xyo.chain.protocol.rpc.e2e

import kotlinx.coroutines.delay
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import network.xyo.chain.protocol.transaction.HydratedTransaction
import network.xyo.chain.protocol.transaction.SignedHydratedTransaction
import network.xyo.chain.protocol.transaction.SignedTransactionBoundWitness
import network.xyo.chain.protocol.viewer.BlockViewer
import network.xyo.chain.protocol.viewer.AccountBalanceViewer
import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.client.account.Wallet
import network.xyo.client.account.model.Wallet as WalletInstance
import network.xyo.client.lib.JsonSerializable

internal object E2eRpcSupport {
    private const val DEFAULT_GENESIS_MNEMONIC = "test test test test test test test test test test test junk"
    private const val GENESIS_ACCOUNT_PATH = "m/44'/60'/0'/0/0"

    fun transport(): HttpRpcTransport {
        val url = System.getenv("XL1_E2E_RPC_URL") ?: error("XL1_E2E_RPC_URL not set")
        return HttpRpcTransport(url)
    }

    fun genesisRewardWallet(): WalletInstance {
        val mnemonic = System.getenv("XL1_E2E_GENESIS_MNEMONIC") ?: DEFAULT_GENESIS_MNEMONIC
        return Wallet.fromMnemonic(mnemonic, GENESIS_ACCOUNT_PATH)
    }

    fun toSignedTransaction(transaction: HydratedTransaction): SignedHydratedTransaction {
        val boundWitness = transaction.boundWitness
        return SignedHydratedTransaction(
            boundWitness = SignedTransactionBoundWitness(
                from = boundWitness.from,
                chain = boundWitness.chain,
                nbf = boundWitness.nbf,
                exp = boundWitness.exp,
                fees = boundWitness.fees,
                script = boundWitness.script,
                addresses = boundWitness.addresses,
                payload_hashes = boundWitness.payload_hashes,
                payload_schemas = boundWitness.payload_schemas,
                previous_hashes = boundWitness.previous_hashes,
                schema = boundWitness.schema,
                timestamp = boundWitness.timestamp,
                signatures = boundWitness.signatures ?: emptyList(),
            ),
            payloads = transaction.payloads,
        )
    }

    fun transactionHash(boundWitness: SignedTransactionBoundWitness): String {
        val hashable = HashableSignedTransactionBoundWitness(
            from = boundWitness.from,
            chain = boundWitness.chain,
            nbf = boundWitness.nbf,
            exp = boundWitness.exp,
            fees = boundWitness.fees,
            script = boundWitness.script,
            addresses = boundWitness.addresses,
            payload_hashes = boundWitness.payload_hashes,
            payload_schemas = boundWitness.payload_schemas,
            previous_hashes = boundWitness.previous_hashes,
            schema = boundWitness.schema,
            timestamp = boundWitness.timestamp,
            signatures = boundWitness.signatures,
        )
        return JsonSerializable.sha256String(hashable, JsonSerializable.Companion.MetaExclusion.STORAGE_META)
    }

    suspend fun awaitBalanceAtLeast(
        viewer: AccountBalanceViewer,
        address: String,
        expectedMinimum: AttoXL1,
        maxAttempts: Int = 60,
        pollIntervalMs: Long = 1000L,
    ): ChainQualified<Map<String, AttoXL1>> {
        repeat(maxAttempts) {
            val balances = viewer.qualifiedAccountBalances(listOf(address), AccountBalanceConfig())
            val balance = balances.data[address] ?: AttoXL1.ZERO
            if (balance >= expectedMinimum) return balances
            delay(pollIntervalMs)
        }
        error("Timed out waiting for $address balance >= $expectedMinimum")
    }

    suspend fun awaitBlockAtLeast(
        viewer: BlockViewer,
        expectedMinimum: Long,
        maxAttempts: Int = 60,
        pollIntervalMs: Long = 1000L,
    ): Long {
        repeat(maxAttempts) {
            val currentBlock = viewer.currentBlock().boundWitness.block
            if (currentBlock >= expectedMinimum) return currentBlock
            delay(pollIntervalMs)
        }
        error("Timed out waiting for block >= $expectedMinimum")
    }
}

@JsonClass(generateAdapter = true)
data class HashableSignedTransactionBoundWitness(
    val from: String,
    val chain: String,
    val nbf: Long,
    val exp: Long,
    val fees: network.xyo.chain.protocol.transaction.TransactionFeesHex,
    val script: List<String>? = null,
    val addresses: List<String> = emptyList(),
    val payload_hashes: List<String> = emptyList(),
    val payload_schemas: List<String> = emptyList(),
    val previous_hashes: List<String?> = emptyList(),
    val schema: String,
    val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String> = emptyList(),
) : JsonSerializable()
