package network.xyo.chain.protocol.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Transfer(
    val schema: String = SCHEMA,
    val from: String,
    val transfers: Map<String, String>,
    val epoch: Long,
    val context: Map<String, Any>? = null,
) {
    companion object {
        const val SCHEMA = "network.xyo.transfer"
    }
}

@JsonClass(generateAdapter = true)
data class TransferWithHashMeta(
    val schema: String = Transfer.SCHEMA,
    val from: String,
    val transfers: Map<String, String>,
    val epoch: Long,
    val context: Map<String, Any>? = null,
    val hash: String,
)
