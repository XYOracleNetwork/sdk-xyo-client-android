package network.xyo.client.payload

import network.xyo.client.lib.JsonSerializable

/**
 * Sequence construction and comparison for distributed ordering.
 * Per the XYO Yellow Paper Section 1.6.
 *
 * A sequence is a hex-encoded composite key that provides deterministic ordering.
 */
object Sequence {

    /**
     * Build a LocalSequence (32 hex chars = 16 bytes).
     * Format: [epoch: 16 hex][nonce: 16 hex]
     * Where nonce = [index: 8 hex][hashFragment: 8 hex]
     *
     * @param timestamp millisecond epoch timestamp
     * @param index payload position in a batch (0-based)
     * @param hash the payload's hash (last 4 bytes used as fragment)
     */
    fun local(timestamp: Long, index: Int, hash: ByteArray): String {
        val epoch = timestamp.toULong().toString(16).padStart(16, '0')
        val indexHex = index.toUInt().toString(16).padStart(8, '0')
        val hashFragment = if (hash.size >= 4) {
            JsonSerializable.bytesToHex(hash.copyOfRange(hash.size - 4, hash.size))
        } else {
            JsonSerializable.bytesToHex(hash).padStart(8, '0')
        }
        return "$epoch$indexHex$hashFragment"
    }

    /**
     * Build a QualifiedSequence (72 hex chars = 36 bytes).
     * Format: [localSequence: 32 hex][address: 40 hex]
     *
     * @param timestamp millisecond epoch timestamp
     * @param index payload position in a batch
     * @param hash the payload's hash
     * @param address 20-byte signer address
     */
    fun qualified(timestamp: Long, index: Int, hash: ByteArray, address: ByteArray): String {
        val localSeq = local(timestamp, index, hash)
        val addressHex = JsonSerializable.bytesToHex(address)
        return "$localSeq$addressHex"
    }

    /**
     * Compare two sequences lexicographically.
     * Works correctly because the epoch (most significant) comes first.
     * Returns negative if a < b, zero if equal, positive if a > b.
     */
    fun compare(a: String, b: String): Int {
        return a.compareTo(b)
    }
}
