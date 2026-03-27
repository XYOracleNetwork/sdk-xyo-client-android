package network.xyo.chain.protocol.block

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.client.boundwitness.model.BoundWitnessFields
import network.xyo.client.boundwitness.model.BoundWitnessMeta

interface BlockBoundWitnessFields {
    val block: Long
    val chain: ChainId
    val previous: String?
    val protocol: Int?
    val step_hashes: List<String>?
    val epoch: Long
}

@JsonClass(generateAdapter = true)
data class BlockBoundWitness(
    override val block: Long,
    override val chain: ChainId,
    override val previous: String?,
    override val protocol: Int? = null,
    override val step_hashes: List<String>? = null,
    @Json(name = "\$epoch") override val epoch: Long,
    override val addresses: List<String> = emptyList(),
    override val payload_hashes: List<String> = emptyList(),
    override val payload_schemas: List<String> = emptyList(),
    override val previous_hashes: List<String?> = emptyList(),
    override val schema: String = SCHEMA,
    override val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String>? = null,
) : BlockBoundWitnessFields, BoundWitnessFields, BoundWitnessMeta {

    override val __signatures: List<String> get() = signatures ?: emptyList()

    val blockNumber: XL1BlockNumber get() = XL1BlockNumber(block)

    companion object {
        const val SCHEMA = "network.xyo.boundwitness"
    }
}

@JsonClass(generateAdapter = true)
data class SignedBlockBoundWitness(
    val block: Long,
    val chain: ChainId,
    val previous: String?,
    val protocol: Int? = null,
    val step_hashes: List<String>? = null,
    @Json(name = "\$epoch") val epoch: Long,
    val addresses: List<String> = emptyList(),
    val payload_hashes: List<String> = emptyList(),
    val payload_schemas: List<String> = emptyList(),
    val previous_hashes: List<String?> = emptyList(),
    val schema: String = BlockBoundWitness.SCHEMA,
    val timestamp: Long? = null,
    @Json(name = "\$signatures") val signatures: List<String> = emptyList(),
)

data class BlockBoundWitnessWithHashMeta(
    val boundWitness: BlockBoundWitness,
    val hash: String,
)

data class SignedBlockBoundWitnessWithHashMeta(
    val boundWitness: SignedBlockBoundWitness,
    val hash: String,
)
