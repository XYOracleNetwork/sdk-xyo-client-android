package network.xyo.chain.protocol.xl1

import java.math.BigInteger

/**
 * The smallest XL1 denomination (0 decimal places).
 * All internal calculations use AttoXL1 as the base unit.
 */
@JvmInline
value class AttoXL1(val value: BigInteger) : Comparable<AttoXL1> {
    init {
        require(value >= BigInteger.ZERO) { "AttoXL1 value must be non-negative" }
        require(value <= MAX_VALUE) { "AttoXL1 value exceeds maximum: $MAX_VALUE" }
    }

    operator fun plus(other: AttoXL1): AttoXL1 = AttoXL1(value + other.value)
    operator fun minus(other: AttoXL1): AttoXL1 = AttoXL1(value - other.value)
    override fun compareTo(other: AttoXL1): Int = value.compareTo(other.value)

    companion object {
        val MAX_VALUE: BigInteger = xl1MaxValue(XL1Places.atto)
        val ZERO = AttoXL1(BigInteger.ZERO)

        fun of(value: BigInteger): AttoXL1 = AttoXL1(value)
        fun ofOrNull(value: BigInteger): AttoXL1? = runCatching { AttoXL1(value) }.getOrNull()

        fun fromHex(hex: String): AttoXL1 = AttoXL1(BigInteger(hex.removePrefix("0x"), 16))
        fun fromHexOrNull(hex: String): AttoXL1? = runCatching { fromHex(hex) }.getOrNull()
    }

    fun toHex(): String = "0x${value.toString(16)}"
    override fun toString(): String = value.toString()
}
