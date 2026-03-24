package network.xyo.chain.protocol.xl1

import java.math.BigInteger

/**
 * The largest XL1 denomination (18 decimal places from AttoXL1).
 * 1 XL1 = 10^18 AttoXL1
 */
@JvmInline
value class XL1(val value: BigInteger) : Comparable<XL1> {
    init {
        require(value >= BigInteger.ZERO) { "XL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "XL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: XL1): XL1 = XL1(value + other.value)
    operator fun minus(other: XL1): XL1 = XL1(value - other.value)
    override fun compareTo(other: XL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.xl1)
        val ZERO = XL1(BigInteger.ZERO)

        fun of(value: BigInteger): XL1 = XL1(value)
        fun ofOrNull(value: BigInteger): XL1? = runCatching { XL1(value) }.getOrNull()
    }

    fun toAtto(): AttoXL1 = AttoXL1(value * AttoXL1ConvertFactor.xl1)
    override fun toString(): String = value.toString()
}
