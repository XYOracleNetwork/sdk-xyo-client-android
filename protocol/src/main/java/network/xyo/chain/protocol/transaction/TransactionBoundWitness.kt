package network.xyo.chain.protocol.transaction

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId

interface TransactionBoundWitnessFields {
    val from: String
    val chain: ChainId
    val nbf: Long
    val exp: Long
    val fees: TransactionFeesHex
    val script: List<String>?
}

@JsonClass(generateAdapter = true)
data class TransactionBoundWitness(
    override val from: String,
    override val chain: ChainId,
    override val nbf: Long,
    override val exp: Long,
    override val fees: TransactionFeesHex,
    override val script: List<String>? = null,
    val addresses: List<String> = emptyList(),
    val payload_hashes: List<String> = emptyList(),
    val payload_schemas: List<String> = emptyList(),
    val previous_hashes: List<String?> = emptyList(),
    val schema: String = SCHEMA,
    val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String>? = null,
) : TransactionBoundWitnessFields {

    val nbfBlockNumber: XL1BlockNumber get() = XL1BlockNumber(nbf)
    val expBlockNumber: XL1BlockNumber get() = XL1BlockNumber(exp)

    companion object {
        const val SCHEMA = "network.xyo.boundwitness"
    }
}

@JsonClass(generateAdapter = true)
data class SignedTransactionBoundWitness(
    val from: String,
    val chain: ChainId,
    val nbf: Long,
    val exp: Long,
    val fees: TransactionFeesHex,
    val script: List<String>? = null,
    val addresses: List<String> = emptyList(),
    val payload_hashes: List<String> = emptyList(),
    val payload_schemas: List<String> = emptyList(),
    val previous_hashes: List<String?> = emptyList(),
    val schema: String = TransactionBoundWitness.SCHEMA,
    val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String> = emptyList(),
)
