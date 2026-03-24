package network.xyo.chain.protocol.xl1

import java.math.BigInteger

@JvmInline
value class NanoXL1(val value: BigInteger) : Comparable<NanoXL1> {
    init {
        require(value >= BigInteger.ZERO) { "NanoXL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "NanoXL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: NanoXL1): NanoXL1 = NanoXL1(value + other.value)
    operator fun minus(other: NanoXL1): NanoXL1 = NanoXL1(value - other.value)
    override fun compareTo(other: NanoXL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.nano)
        val ZERO = NanoXL1(BigInteger.ZERO)

        fun of(value: BigInteger): NanoXL1 = NanoXL1(value)
        fun ofOrNull(value: BigInteger): NanoXL1? = runCatching { NanoXL1(value) }.getOrNull()
    }

    fun toAtto(): AttoXL1 = AttoXL1(value * AttoXL1ConvertFactor.nano)
    override fun toString(): String = value.toString()
}
