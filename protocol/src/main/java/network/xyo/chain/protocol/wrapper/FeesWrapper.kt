package network.xyo.chain.protocol.wrapper

import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import network.xyo.chain.protocol.xl1.AttoXL1
import java.math.BigInteger

/**
 * Wrapper for transaction fees with convenient accessors, matching JS Fees wrapper.
 */
class FeesWrapper(val fees: TransactionFeesBigInt) {

    val base: AttoXL1 get() = AttoXL1(fees.base)
    val gasLimit: AttoXL1 get() = AttoXL1(fees.gasLimit)
    val gasPrice: AttoXL1 get() = AttoXL1(fees.gasPrice)
    val priority: AttoXL1 get() = AttoXL1(fees.priority)

    /** Total fee = base + (gasLimit * gasPrice) + priority */
    val total: AttoXL1
        get() = AttoXL1(fees.base + (fees.gasLimit * fees.gasPrice) + fees.priority)

    override fun toString(): String {
        return "Fees(base=${fees.base}, gasLimit=${fees.gasLimit}, gasPrice=${fees.gasPrice}, priority=${fees.priority}, total=${total.value})"
    }

    companion object {
        fun fromBigInts(base: BigInteger, gasLimit: BigInteger, gasPrice: BigInteger, priority: BigInteger): FeesWrapper {
            return FeesWrapper(TransactionFeesBigInt(base, gasLimit, gasPrice, priority))
        }
    }
}
