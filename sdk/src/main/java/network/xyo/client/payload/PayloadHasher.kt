package network.xyo.client.payload

import network.xyo.client.lib.JsonSerializable
import network.xyo.client.types.Hash
import network.xyo.client.types.HashHex
import java.security.MessageDigest

/**
 * Dedicated hash utility for payloads, matching JS ObjectHasher/PayloadHasher.
 * Extracts hash logic from JsonSerializable into a focused utility.
 */
object PayloadHasher {

    /**
     * Compute SHA-256 hash of a raw string.
     */
    fun sha256(value: String): Hash {
        val md = MessageDigest.getInstance("SHA256")
        md.update(value.encodeToByteArray())
        return md.digest()
    }

    /**
     * Compute SHA-256 hash of raw bytes.
     */
    fun sha256Bytes(bytes: ByteArray): Hash {
        val md = MessageDigest.getInstance("SHA256")
        md.update(bytes)
        return md.digest()
    }

    /**
     * Compute the data hash of a payload (excludes meta fields starting with _ or $).
     */
    fun <T : JsonSerializable> dataHash(obj: T): Hash {
        val json = JsonSerializable.toJson(obj, removeMeta = true)
        return sha256(json)
    }

    /**
     * Compute the full hash of a payload (includes all fields).
     */
    fun <T : JsonSerializable> hash(obj: T): Hash {
        val json = JsonSerializable.toJson(obj, removeMeta = false)
        return sha256(json)
    }

    /**
     * Compute the data hash and return as hex string.
     */
    fun <T : JsonSerializable> dataHashHex(obj: T): HashHex {
        return bytesToHex(dataHash(obj))
    }

    /**
     * Compute the full hash and return as hex string.
     */
    fun <T : JsonSerializable> hashHex(obj: T): HashHex {
        return bytesToHex(hash(obj))
    }

    /**
     * Compute data hashes for a list of payloads.
     */
    fun <T : JsonSerializable> dataHashes(payloads: List<T>): List<Hash> {
        return payloads.map { dataHash(it) }
    }

    /**
     * Compute full hashes for a list of payloads.
     */
    fun <T : JsonSerializable> hashes(payloads: List<T>): List<Hash> {
        return payloads.map { hash(it) }
    }

    /**
     * Compute data hashes as hex strings for a list of payloads.
     */
    fun <T : JsonSerializable> dataHashesHex(payloads: List<T>): List<HashHex> {
        return payloads.map { dataHashHex(it) }
    }

    /**
     * Compute full hashes as hex strings for a list of payloads.
     */
    fun <T : JsonSerializable> hashesHex(payloads: List<T>): List<HashHex> {
        return payloads.map { hashHex(it) }
    }

    /**
     * Compute hash pairs (payload, hash) for a list of payloads.
     */
    fun <T : JsonSerializable> hashPairs(payloads: List<T>): List<Pair<T, Hash>> {
        return payloads.map { Pair(it, hash(it)) }
    }

    /**
     * Compute data hash pairs (payload, dataHash) for a list of payloads.
     */
    fun <T : JsonSerializable> dataHashPairs(payloads: List<T>): List<Pair<T, Hash>> {
        return payloads.map { Pair(it, dataHash(it)) }
    }

    /**
     * Create a map from hash hex -> payload.
     */
    fun <T : JsonSerializable> toHashMap(payloads: List<T>): Map<HashHex, T> {
        return payloads.associateBy { hashHex(it) }
    }

    /**
     * Create a map from data hash hex -> payload.
     */
    fun <T : JsonSerializable> toDataHashMap(payloads: List<T>): Map<HashHex, T> {
        return payloads.associateBy { dataHashHex(it) }
    }

    /**
     * Filter payloads, excluding those whose hash matches any in the given list.
     */
    fun <T : JsonSerializable> filterExclude(payloads: List<T>, hashesToExclude: List<HashHex>): List<T> {
        val excludeSet = hashesToExclude.toSet()
        return payloads.filter { hashHex(it) !in excludeSet }
    }

    /**
     * Filter payloads, excluding those whose data hash matches any in the given list.
     */
    fun <T : JsonSerializable> filterExcludeByDataHash(payloads: List<T>, hashesToExclude: List<HashHex>): List<T> {
        val excludeSet = hashesToExclude.toSet()
        return payloads.filter { dataHashHex(it) !in excludeSet }
    }

    /**
     * Filter payloads, including only those whose data hash matches any in the given list.
     */
    fun <T : JsonSerializable> filterIncludeByDataHash(payloads: List<T>, hashesToInclude: List<HashHex>): List<T> {
        val includeSet = hashesToInclude.toSet()
        return payloads.filter { dataHashHex(it) in includeSet }
    }

    /**
     * Find a payload by its data hash.
     */
    fun <T : JsonSerializable> findByDataHash(payloads: List<T>, targetHash: HashHex): T? {
        return payloads.find { dataHashHex(it) == targetHash }
    }

    /**
     * Find a payload by its full hash.
     */
    fun <T : JsonSerializable> findByHash(payloads: List<T>, targetHash: HashHex): T? {
        return payloads.find { hashHex(it) == targetHash }
    }

    /**
     * Get the JSON representation of hashable fields (sorted, meta stripped).
     */
    fun <T : JsonSerializable> hashableFields(obj: T): String {
        return JsonSerializable.toJson(obj, removeMeta = true)
    }

    private val hexArray = "0123456789abcdef".toCharArray()

    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}
