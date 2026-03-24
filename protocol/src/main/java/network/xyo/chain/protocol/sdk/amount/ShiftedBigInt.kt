package network.xyo.chain.protocol.sdk.amount

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class ShiftedBigIntConfig(
    val places: Int = 18,
    val maxDecimal: Int = 18,
    val maxCharacters: Int = 9,
    val minDecimals: Int = 1,
    val locale: Locale = Locale.US,
)

class ShiftedBigInt(
    val value: BigInteger,
    val config: ShiftedBigIntConfig = ShiftedBigIntConfig(),
) {
    fun toFullString(): String = formatDecimal(
        value = value,
        places = config.places,
        maxDecimal = config.places,
        maxCharacters = Int.MAX_VALUE,
        minDecimals = config.places,
        locale = config.locale,
    )

    fun toShortString(): String = formatDecimal(
        value = value,
        places = config.places,
        maxDecimal = config.maxDecimal,
        maxCharacters = config.maxCharacters,
        minDecimals = config.minDecimals,
        locale = config.locale,
    )

    override fun toString(): String = toShortString()

    companion object {
        fun formatDecimal(
            value: BigInteger,
            places: Int,
            maxDecimal: Int,
            maxCharacters: Int,
            minDecimals: Int,
            locale: Locale,
        ): String {
            if (places == 0) {
                val symbols = DecimalFormatSymbols(locale)
                val format = DecimalFormat("#,##0", symbols)
                return format.format(value)
            }

            val decimal = BigDecimal(value).divide(
                BigDecimal.TEN.pow(places),
                maxDecimal.coerceAtMost(places),
                RoundingMode.FLOOR,
            )

            val intPart = decimal.toBigInteger().abs()
            val intStr = intPart.toString()

            val availableDecimals = (maxCharacters - intStr.length - 1).coerceAtLeast(minDecimals).coerceAtMost(maxDecimal)

            val scaled = decimal.setScale(availableDecimals, RoundingMode.FLOOR)

            val symbols = DecimalFormatSymbols(locale)
            val pattern = buildString {
                append("#,##0")
                if (availableDecimals > 0) {
                    append(".")
                    repeat(minDecimals.coerceAtMost(availableDecimals)) { append("0") }
                    repeat(availableDecimals - minDecimals.coerceAtMost(availableDecimals)) { append("#") }
                }
            }
            val format = DecimalFormat(pattern, symbols)
            return format.format(scaled)
        }
    }
}
