package network.xyo.chain.protocol.xl1

import java.math.BigInteger

@JvmInline
value class PicoXL1(val value: BigInteger) : Comparable<PicoXL1> {
    init {
        require(value >= BigInteger.ZERO) { "PicoXL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "PicoXL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: PicoXL1): PicoXL1 = PicoXL1(value + other.value)
    operator fun minus(other: PicoXL1): PicoXL1 = PicoXL1(value - other.value)
    override fun compareTo(other: PicoXL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.pico)
        val ZERO = PicoXL1(BigInteger.ZERO)

        fun of(value: BigInteger): PicoXL1 = PicoXL1(value)
        fun ofOrNull(value: BigInteger): PicoXL1? = runCatching { PicoXL1(value) }.getOrNull()
    }

    fun toAtto(): AttoXL1 = AttoXL1(value * AttoXL1ConvertFactor.pico)
    override fun toString(): String = value.toString()
}
