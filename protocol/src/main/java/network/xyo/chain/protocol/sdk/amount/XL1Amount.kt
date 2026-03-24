package network.xyo.chain.protocol.sdk.amount

import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.chain.protocol.xl1.AttoXL1ConvertFactor
import network.xyo.chain.protocol.xl1.FemtoXL1
import network.xyo.chain.protocol.xl1.MicroXL1
import network.xyo.chain.protocol.xl1.MilliXL1
import network.xyo.chain.protocol.xl1.NanoXL1
import network.xyo.chain.protocol.xl1.PicoXL1
import network.xyo.chain.protocol.xl1.XL1
import network.xyo.chain.protocol.xl1.XL1Places
import java.math.BigInteger
import java.util.Locale

/**
 * High-level wrapper for XL1 amounts. Internally stores value as AttoXL1 (smallest unit).
 * Provides conversions to all denominations and locale-aware formatting.
 */
class XL1Amount private constructor(
    val value: AttoXL1,
    private val locale: Locale = Locale.US,
) {
    val atto: AttoXL1 get() = value
    val femto: FemtoXL1 get() = FemtoXL1(value.value / AttoXL1ConvertFactor.femto)
    val pico: PicoXL1 get() = PicoXL1(value.value / AttoXL1ConvertFactor.pico)
    val nano: NanoXL1 get() = NanoXL1(value.value / AttoXL1ConvertFactor.nano)
    val micro: MicroXL1 get() = MicroXL1(value.value / AttoXL1ConvertFactor.micro)
    val milli: MilliXL1 get() = MilliXL1(value.value / AttoXL1ConvertFactor.milli)
    val xl1: XL1 get() = XL1(value.value / AttoXL1ConvertFactor.xl1)

    fun toString(places: Int = XL1Places.atto, config: ShiftedBigIntConfig = ShiftedBigIntConfig(places = places, locale = locale)): String {
        return ShiftedBigInt(value.value, config).toShortString()
    }

    fun toFullString(places: Int = XL1Places.atto): String {
        return ShiftedBigInt(value.value, ShiftedBigIntConfig(places = places, locale = locale)).toFullString()
    }

    override fun toString(): String = toString(XL1Places.xl1)

    operator fun plus(other: XL1Amount): XL1Amount = XL1Amount(value + other.value, locale)
    operator fun minus(other: XL1Amount): XL1Amount = XL1Amount(value - other.value, locale)
    operator fun compareTo(other: XL1Amount): Int = value.compareTo(other.value)

    companion object {
        private val MAX_ATTO = AttoXL1.MAX_VALUE

        fun fromAtto(value: BigInteger, locale: Locale = Locale.US): XL1Amount {
            val clamped = value.coerceIn(BigInteger.ZERO, MAX_ATTO)
            return XL1Amount(AttoXL1(clamped), locale)
        }

        fun fromAtto(value: AttoXL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value, locale)
        fun fromFemto(value: FemtoXL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value.toAtto(), locale)
        fun fromPico(value: PicoXL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value.toAtto(), locale)
        fun fromNano(value: NanoXL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value.toAtto(), locale)
        fun fromMicro(value: MicroXL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value.toAtto(), locale)
        fun fromMilli(value: MilliXL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value.toAtto(), locale)
        fun fromXL1(value: XL1, locale: Locale = Locale.US): XL1Amount = XL1Amount(value.toAtto(), locale)

        fun from(value: BigInteger, places: Int = XL1Places.atto, locale: Locale = Locale.US): XL1Amount {
            require(places in XL1Places.all) { "Invalid denomination places: $places" }
            val attoValue = value * BigInteger.TEN.pow(places)
            return fromAtto(attoValue, locale)
        }

        val ZERO = XL1Amount(AttoXL1.ZERO)
    }
}

private fun BigInteger.coerceIn(min: BigInteger, max: BigInteger): BigInteger =
    if (this < min) min else if (this > max) max else this
