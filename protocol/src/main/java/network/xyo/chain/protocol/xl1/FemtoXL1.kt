package network.xyo.chain.protocol.xl1

import java.math.BigInteger

@JvmInline
value class FemtoXL1(val value: BigInteger) : Comparable<FemtoXL1> {
    init {
        require(value >= BigInteger.ZERO) { "FemtoXL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "FemtoXL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: FemtoXL1): FemtoXL1 = FemtoXL1(value + other.value)
    operator fun minus(other: FemtoXL1): FemtoXL1 = FemtoXL1(value - other.value)
    override fun compareTo(other: FemtoXL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.femto)
        val ZERO = FemtoXL1(BigInteger.ZERO)

        fun of(value: BigInteger): FemtoXL1 = FemtoXL1(value)
        fun ofOrNull(value: BigInteger): FemtoXL1? = runCatching { FemtoXL1(value) }.getOrNull()
    }

    fun toAtto(): AttoXL1 = AttoXL1(value * AttoXL1ConvertFactor.femto)
    override fun toString(): String = value.toString()
}
