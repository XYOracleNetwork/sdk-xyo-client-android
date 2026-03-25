package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider

/** Time domain identifiers matching the server's TimeDomain type. */
enum class TimeDomain {
    xl1, epoch, ethereum
}

interface TimeSyncViewer : Provider {
    override val moniker: String get() = MONIKER

    /** Convert a time value from one domain to another. */
    suspend fun convertTime(from: TimeDomain, to: TimeDomain, value: Long): Long

    /** Get the current time in the given domain. Returns [domain, value]. */
    suspend fun currentTime(domain: TimeDomain): Pair<TimeDomain, Long>

    /** Get the current time and optional hash in the given domain. Returns [value, hash?]. */
    suspend fun currentTimeAndHash(domain: TimeDomain): Pair<Long, String?>

    /** Get the current time payload (no params). */
    suspend fun currentTimePayload(): Map<String, Any?>

    companion object {
        const val MONIKER = "TimeSyncViewer"
    }
}
