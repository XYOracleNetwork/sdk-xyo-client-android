package network.xyo.client.payload

/**
 * Base interface for query payload fields.
 * Per the XYO Yellow Paper Section 6.4.
 *
 * All query payloads may include these optional fields to control
 * query routing, budgeting, and frequency.
 */
interface QueryFields {
    /** Target handler address(es). */
    val address: Any?
        get() = null

    /** Maximum XYO expenditure for the query. */
    val budget: Double?
        get() = null

    /** Frequency cap: "once", "second", "minute", "hour", "day", "week", "month", "year". */
    val maxFrequency: String?
        get() = null

    /** Minimum bid for query execution. */
    val minBid: Double?
        get() = null
}
