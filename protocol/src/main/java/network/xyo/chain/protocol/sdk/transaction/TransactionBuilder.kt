package network.xyo.chain.protocol.sdk.transaction

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.transaction.HydratedTransaction
import network.xyo.chain.protocol.transaction.TransactionBoundWitness
import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import network.xyo.client.account.model.Account
import network.xyo.client.boundwitness.BoundWitnessBuilder
import network.xyo.client.payload.Payload

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

        // Use BoundWitnessBuilder to hash payloads and sign
        val bwBuilder = BoundWitnessBuilder()
        bwBuilder.signers(signers)
        bwBuilder.payloads(payloads)

        val (bw, _) = bwBuilder.build()

        val boundWitness = TransactionBoundWitness(
            from = from,
            chain = chainId,
            nbf = nbfBlock.value,
            exp = expBlock.value,
            fees = txFees.toHex(),
            script = scripts.ifEmpty { null },
            addresses = bw.addresses,
            payload_hashes = bw.payload_hashes,
            payload_schemas = bw.payload_schemas,
            previous_hashes = bw.previous_hashes,
            timestamp = bw.timestamp ?: System.currentTimeMillis(),
            signatures = bw.__signatures,
        )

        return HydratedTransaction(
            boundWitness = boundWitness,
            payloads = payloads,
        )
    }
}
