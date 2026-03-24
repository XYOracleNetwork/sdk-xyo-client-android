package network.xyo.chain.protocol.xl1

import java.math.BigInteger

@JvmInline
value class MicroXL1(val value: BigInteger) : Comparable<MicroXL1> {
    init {
        require(value >= BigInteger.ZERO) { "MicroXL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "MicroXL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: MicroXL1): MicroXL1 = MicroXL1(value + other.value)
    operator fun minus(other: MicroXL1): MicroXL1 = MicroXL1(value - other.value)
    override fun compareTo(other: MicroXL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.micro)
        val ZERO = MicroXL1(BigInteger.ZERO)

        fun of(value: BigInteger): MicroXL1 = MicroXL1(value)
        fun ofOrNull(value: BigInteger): MicroXL1? = runCatching { MicroXL1(value) }.getOrNull()
    }

    fun toAtto(): AttoXL1 = AttoXL1(value * AttoXL1ConvertFactor.micro)
    override fun toString(): String = value.toString()
}
