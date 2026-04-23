package network.xyo.chain.protocol.sdk.transaction

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.transaction.HydratedTransaction
import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import network.xyo.chain.protocol.transaction.TransactionFeesHex
import network.xyo.client.account.model.Account
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.payload.Payload
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class TransactionBuilder {
    private var chain: ChainId? = null
    private var fees: TransactionFeesBigInt? = null
    private var nbf: XL1BlockNumber? = null
    private var exp: XL1BlockNumber? = null
    private val payloads: MutableList<Payload> = mutableListOf()
    private val signers: MutableList<Account> = mutableListOf()
    private var fromAddress: String? = null
    private val scripts: MutableList<String> = mutableListOf()

    fun chain(chain: ChainId): TransactionBuilder = apply { this.chain = chain }

    fun fees(fees: TransactionFeesBigInt): TransactionBuilder = apply { this.fees = fees }

    fun range(nbf: XL1BlockNumber, exp: XL1BlockNumber): TransactionBuilder = apply {
        this.nbf = nbf
        this.exp = exp
    }

    fun range(nbf: Long, exp: Long): TransactionBuilder = apply {
        this.nbf = XL1BlockNumber(nbf)
        this.exp = XL1BlockNumber(exp)
    }

    fun payload(payload: Payload): TransactionBuilder = apply { payloads.add(payload) }

    fun payloads(payloads: List<Payload>): TransactionBuilder = apply {
        this.payloads.addAll(payloads)
    }

    fun from(address: String): TransactionBuilder = apply { fromAddress = address }

    fun signer(signer: Account): TransactionBuilder = apply { signers.add(signer) }

    fun signers(signers: List<Account>): TransactionBuilder = apply {
        this.signers.addAll(signers)
    }

    fun script(script: String): TransactionBuilder = apply { scripts.add(script) }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun build(): HydratedTransaction {
        val chainId = requireNotNull(chain) { "Chain must be set" }
        val txFees = requireNotNull(fees) { "Fees must be set" }
        val nbfBlock = requireNotNull(nbf) { "nbf (not before block) must be set" }
        val expBlock = requireNotNull(exp) { "exp (expiration block) must be set" }

        val from = fromAddress
            ?: signers.firstOrNull()?.address?.toHexString()
            ?: error("From address or at least one signer must be provided")

        val payloadHashes = payloads.map { payload -> payload.hash().toHexString() }
        val autoScript = payloadHashes.map { payloadHash -> "elevate|$payloadHash" }
        val transactionScript = (scripts + autoScript).distinct().ifEmpty { null }
        val timestamp = System.currentTimeMillis()
        val addresses = signers.map { signer -> signer.address.toHexString() }
        val previousHashes = signers.map { signer -> signer.previousHash?.toHexString() }
        val normalizedFees = txFees.toHex().normalized()

        val signable = SignableTransactionBoundWitness(
            from = from,
            chain = chainId,
            nbf = nbfBlock.value,
            exp = expBlock.value,
            fees = normalizedFees,
            script = transactionScript,
            addresses = addresses,
            payload_hashes = payloadHashes,
            payload_schemas = payloads.map { payload -> payload.schema },
            previous_hashes = previousHashes,
            timestamp = timestamp,
        )

        val dataHash = JsonSerializable.sha256(signable, JsonSerializable.Companion.MetaExclusion.ALL_META)
        val signatures = signers.map { signer -> JsonSerializable.bytesToHex(signer.sign(dataHash)) }

        val boundWitness = TransactionBoundWitness(
            from = from,
            chain = chainId,
            nbf = nbfBlock.value,
            exp = expBlock.value,
            fees = normalizedFees,
            script = transactionScript,
            addresses = addresses,
            payload_hashes = payloadHashes,
            payload_schemas = payloads.map { payload -> payload.schema },
            previous_hashes = previousHashes,
            timestamp = timestamp,
            signatures = signatures,
        )

        return HydratedTransaction(
            boundWitness = boundWitness,
            payloads = payloads,
        )
    }
}

@JsonClass(generateAdapter = true)
data class SignableTransactionBoundWitness(
    val from: String,
    val chain: ChainId,
    val nbf: Long,
    val exp: Long,
    val fees: network.xyo.chain.protocol.transaction.TransactionFeesHex,
    val script: List<String>? = null,
    val addresses: List<String> = emptyList(),
    val payload_hashes: List<String> = emptyList(),
    val payload_schemas: List<String> = emptyList(),
    val previous_hashes: List<String?> = emptyList(),
    val schema: String = TransactionBoundWitness.SCHEMA,
    val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String> = emptyList(),
) : JsonSerializable()

private fun TransactionFeesHex.normalized(): TransactionFeesHex = TransactionFeesHex(
    base = base.removePrefix("0x").removePrefix("0X"),
    gasLimit = gasLimit.removePrefix("0x").removePrefix("0X"),
    gasPrice = gasPrice.removePrefix("0x").removePrefix("0X"),
    priority = priority.removePrefix("0x").removePrefix("0X"),
)
