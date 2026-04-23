package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import network.xyo.chain.protocol.xl1.AttoXL1ConvertFactor
import java.math.BigInteger

object DefaultTransactionFees {
    val minimum = TransactionFeesBigInt(
        base = BigInteger.valueOf(1000).multiply(AttoXL1ConvertFactor.nano),
        gasLimit = BigInteger.valueOf(1_000_000).multiply(AttoXL1ConvertFactor.nano),
        gasPrice = BigInteger.TEN.multiply(AttoXL1ConvertFactor.nano),
        priority = BigInteger.ZERO,
    )

    val default = TransactionFeesBigInt(
        base = minimum.base,
        gasLimit = minimum.gasLimit,
        gasPrice = minimum.gasPrice,
        priority = minimum.priority,
    )
}
