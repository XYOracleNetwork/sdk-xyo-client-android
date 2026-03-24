package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import java.math.BigInteger

object DefaultTransactionFees {
    val minimum = TransactionFeesBigInt(
        base = BigInteger.ZERO,
        gasLimit = BigInteger.valueOf(21000),
        gasPrice = BigInteger.ONE,
        priority = BigInteger.ZERO,
    )

    val default = TransactionFeesBigInt(
        base = BigInteger.ZERO,
        gasLimit = BigInteger.valueOf(21000),
        gasPrice = BigInteger.ONE,
        priority = BigInteger.ZERO,
    )
}
