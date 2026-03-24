package network.xyo.chain.protocol.xl1

import java.math.BigInteger

@JvmInline
value class MilliXL1(val value: BigInteger) : Comparable<MilliXL1> {
    init {
        require(value >= BigInteger.ZERO) { "MilliXL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "MilliXL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: MilliXL1): MilliXL1 = MilliXL1(value + other.value)
    operator fun minus(other: MilliXL1): MilliXL1 = MilliXL1(value - other.value)
    override fun compareTo(other: MilliXL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.milli)
        val ZERO = MilliXL1(BigInteger.ZERO)

        fun of(value: BigInteger): MilliXL1 = MilliXL1(value)
        fun ofOrNull(value: BigInteger): MilliXL1? = runCatching { MilliXL1(value) }.getOrNull()
    }

    fun toAtto(): AttoXL1 = AttoXL1(value * AttoXL1ConvertFactor.milli)
    override fun toString(): String = value.toString()
}
