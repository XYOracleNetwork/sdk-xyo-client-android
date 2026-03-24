package network.xyo.chain.protocol.transaction

import java.math.BigInteger

data class TransactionFeesBigInt(
    val base: BigInteger,
    val gasLimit: BigInteger,
    val gasPrice: BigInteger,
    val priority: BigInteger,
) {
    fun toHex(): TransactionFeesHex = TransactionFeesHex(
        base = "0x${base.toString(16)}",
        gasLimit = "0x${gasLimit.toString(16)}",
        gasPrice = "0x${gasPrice.toString(16)}",
        priority = "0x${priority.toString(16)}",
    )

    companion object {
        fun fromHex(hex: TransactionFeesHex): TransactionFeesBigInt = TransactionFeesBigInt(
            base = BigInteger(hex.base.removePrefix("0x"), 16),
            gasLimit = BigInteger(hex.gasLimit.removePrefix("0x"), 16),
            gasPrice = BigInteger(hex.gasPrice.removePrefix("0x"), 16),
            priority = BigInteger(hex.priority.removePrefix("0x"), 16),
        )
    }
}
