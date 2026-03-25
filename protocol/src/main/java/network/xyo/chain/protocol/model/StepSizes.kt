package network.xyo.chain.protocol.model

import java.math.BigInteger

object StepSizes {
    val values: List<Long> = listOf(
        7L,
        31L,
        211L,
        2_311L,
        30_031L,
        510_511L,
        9_699_691L,
        223_092_871L,
        6_469_693_231L,
    )

    val length: Int get() = values.size

    fun stepSize(step: Int): Long {
        require(step in values.indices) { "Invalid step index: $step" }
        return values[step]
    }

    val rewardFractions: List<Pair<BigInteger, BigInteger>> = listOf(
        BigInteger.ZERO to BigInteger.ONE,
        BigInteger.ZERO to BigInteger.ONE,
        BigInteger.ZERO to BigInteger.ONE,
        BigInteger.ONE to BigInteger.valueOf(10_000),
        BigInteger.valueOf(2) to BigInteger.valueOf(1_000),
        BigInteger.valueOf(3) to BigInteger.valueOf(100),
        BigInteger.valueOf(45) to BigInteger.valueOf(100),
    )
}
