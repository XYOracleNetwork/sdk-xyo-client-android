package network.xyo.chain.protocol.transaction

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionFeesHex(
    val base: String,
    val gasLimit: String,
    val gasPrice: String,
    val priority: String,
) {
    fun toBigInt(): TransactionFeesBigInt = TransactionFeesBigInt.fromHex(this)
}
