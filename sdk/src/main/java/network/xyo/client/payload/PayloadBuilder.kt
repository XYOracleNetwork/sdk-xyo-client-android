package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.types.Hash
import network.xyo.client.types.HashHex

/**
 * Fluent builder for creating Payload instances, matching JS PayloadBuilder.
 *
 * Usage:
 * ```kotlin
 * val payload = PayloadBuilder("network.xyo.example")
 *     .fields("key" to "value", "count" to 42)
 *     .build()
 * ```
 */
class PayloadBuilder(private var _schema: String) {

    private val _fields = mutableMapOf<String, Any?>()

    fun schema(schema: String): PayloadBuilder {
        _schema = schema
        return this
    }

    fun fields(vararg pairs: Pair<String, Any?>): PayloadBuilder {
        _fields.putAll(pairs)
        return this
    }

    fun fields(map: Map<String, Any?>): PayloadBuilder {
        _fields.putAll(map)
        return this
    }

    fun field(key: String, value: Any?): PayloadBuilder {
        _fields[key] = value
        return this
    }

    fun build(): Payload {
        return Payload(_schema)
    }

    companion object {
        /**
         * Compute data hash for a payload.
         */
        fun dataHash(payload: Payload): Hash = PayloadHasher.dataHash(payload)

        /**
         * Compute data hash as hex string for a payload.
         */
        fun dataHashHex(payload: Payload): HashHex = PayloadHasher.dataHashHex(payload)

        /**
         * Compute full hash for a payload.
         */
        fun hash(payload: Payload): Hash = PayloadHasher.hash(payload)

        /**
         * Compute full hash as hex string for a payload.
         */
        fun hashHex(payload: Payload): HashHex = PayloadHasher.hashHex(payload)

        /**
         * Compute data hashes for a list of payloads.
         */
        fun dataHashes(payloads: List<Payload>): List<Hash> = PayloadHasher.dataHashes(payloads)

        /**
         * Compute data hashes as hex strings for a list of payloads.
         */
        fun dataHashesHex(payloads: List<Payload>): List<HashHex> = PayloadHasher.dataHashesHex(payloads)

        /**
         * Compute hash pairs (payload, hash) for a list of payloads.
         */
        fun hashPairs(payloads: List<Payload>): List<Pair<Payload, Hash>> = PayloadHasher.hashPairs(payloads)

        /**
         * Compute data hash pairs (payload, dataHash) for a list of payloads.
         */
        fun dataHashPairs(payloads: List<Payload>): List<Pair<Payload, Hash>> = PayloadHasher.dataHashPairs(payloads)

        /**
         * Create a hash map from hash hex -> payload.
         */
        fun toHashMap(payloads: List<Payload>): Map<HashHex, Payload> = PayloadHasher.toHashMap(payloads)

        /**
         * Create a data hash map from data hash hex -> payload.
         */
        fun toDataHashMap(payloads: List<Payload>): Map<HashHex, Payload> = PayloadHasher.toDataHashMap(payloads)

        /**
         * Filter payloads, excluding those matching the given hashes.
         */
        fun filterExclude(payloads: List<Payload>, hashes: List<HashHex>): List<Payload> =
            PayloadHasher.filterExclude(payloads, hashes)

        /**
         * Filter payloads, excluding those whose data hash matches.
         */
        fun filterExcludeByDataHash(payloads: List<Payload>, hashes: List<HashHex>): List<Payload> =
            PayloadHasher.filterExcludeByDataHash(payloads, hashes)

        /**
         * Filter payloads, including only those whose data hash matches.
         */
        fun filterIncludeByDataHash(payloads: List<Payload>, hashes: List<HashHex>): List<Payload> =
            PayloadHasher.filterIncludeByDataHash(payloads, hashes)

        /**
         * Find a payload by its data hash.
         */
        fun findByDataHash(payloads: List<Payload>, hash: HashHex): Payload? =
            PayloadHasher.findByDataHash(payloads, hash)

        /**
         * Find a payload by its full hash.
         */
        fun findByHash(payloads: List<Payload>, hash: HashHex): Payload? =
            PayloadHasher.findByHash(payloads, hash)
    }
}
