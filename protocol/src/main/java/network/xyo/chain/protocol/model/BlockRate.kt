package network.xyo.chain.protocol.model

data class TimeDurations(
    val millis: Double = 0.0,
    val seconds: Double = 0.0,
    val minutes: Double = 0.0,
    val hours: Double = 0.0,
    val days: Double = 0.0,
    val weeks: Double = 0.0,
)

enum class TimeUnit {
    millis, seconds, minutes, hours, days, weeks
}

data class BlockRate(
    val range: XL1BlockRange,
    val rate: Double,
    val timeUnit: TimeUnit,
    val span: Long,
    val timeDifference: Double,
    val timePerBlock: Double,
)

data class TimeConfig(
    val minutes: Double? = null,
    val hours: Double? = null,
    val days: Double? = null,
    val weeks: Double? = null,
    val months: Double? = null,
    val years: Double? = null,
)
