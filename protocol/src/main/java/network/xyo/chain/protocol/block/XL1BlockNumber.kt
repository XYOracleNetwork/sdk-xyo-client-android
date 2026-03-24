package network.xyo.chain.protocol.block

@JvmInline
value class XL1BlockNumber(val value: Long) : Comparable<XL1BlockNumber> {
    init {
        require(value >= 0) { "Block number must be non-negative" }
    }

    operator fun plus(other: Long): XL1BlockNumber = XL1BlockNumber(value + other)
    operator fun minus(other: XL1BlockNumber): Long = value - other.value
    override fun compareTo(other: XL1BlockNumber): Int = value.compareTo(other.value)

    companion object {
        val ZERO = XL1BlockNumber(0)

        fun of(value: Long): XL1BlockNumber = XL1BlockNumber(value)
        fun of(value: Int): XL1BlockNumber = XL1BlockNumber(value.toLong())
        fun ofOrNull(value: Long): XL1BlockNumber? = runCatching { XL1BlockNumber(value) }.getOrNull()
    }

    override fun toString(): String = value.toString()
}
