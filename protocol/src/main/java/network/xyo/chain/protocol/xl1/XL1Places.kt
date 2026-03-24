package network.xyo.chain.protocol.xl1

import java.math.BigInteger

/**
 * Decimal places for each XL1 denomination.
 * AttoXL1 is the smallest unit (0 decimal places from base).
 * XL1 is the largest unit (18 decimal places from base).
 */
object XL1Places {
    val atto: Int = 0
    val femto: Int = 3
    val pico: Int = 6
    val nano: Int = 9
    val micro: Int = 12
    val milli: Int = 15
    val xl1: Int = 18

    val all: List<Int> = listOf(atto, femto, pico, nano, micro, milli, xl1)
}

/**
 * Conversion factors from AttoXL1 to each denomination.
 */
object AttoXL1ConvertFactor {
    val xl1: BigInteger = BigInteger.TEN.pow(XL1Places.xl1)
    val milli: BigInteger = BigInteger.TEN.pow(XL1Places.milli)
    val micro: BigInteger = BigInteger.TEN.pow(XL1Places.micro)
    val nano: BigInteger = BigInteger.TEN.pow(XL1Places.nano)
    val pico: BigInteger = BigInteger.TEN.pow(XL1Places.pico)
    val femto: BigInteger = BigInteger.TEN.pow(XL1Places.femto)
    val atto: BigInteger = BigInteger.ONE
}

/**
 * Calculate the maximum value for a given denomination.
 * maxValue = 10^(32 - places) - 1
 */
fun xl1MaxValue(places: Int): BigInteger = BigInteger.TEN.pow(32 - places) - BigInteger.ONE
