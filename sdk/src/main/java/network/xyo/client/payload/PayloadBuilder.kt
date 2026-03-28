package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.lib.JsonSerializable.Companion.MetaExclusion
import network.xyo.client.types.Hash
import network.xyo.client.types.HashHex
import org.json.JSONObject

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

        // --- Metadata Management (Yellow Paper Section 12.1) ---

        /**
         * Get the hashable fields of a payload (storage meta removed).
         * This is the canonical form used for `hash()` computation.
         */
        fun hashableFields(payload: Payload): String =
            JsonSerializable.toJson(payload, MetaExclusion.STORAGE_META)

        /**
         * Get the data-hashable fields of a payload (all meta removed).
         * This is the canonical form used for `dataHash()` computation.
         */
        fun dataHashableFields(payload: Payload): String =
            JsonSerializable.toJson(payload, MetaExclusion.ALL_META)

        /**
         * Remove storage metadata (`_` prefix fields) from a JSON string.
         */
        fun omitStorageMeta(json: String): String =
            JsonSerializable.sortJson(json, MetaExclusion.STORAGE_META)

        /**
         * Remove client metadata (`$` prefix fields) from a JSON string.
         */
        fun omitClientMeta(json: String): String {
            val obj = JSONObject(json)
            val keys = obj.keys().asSequence().filter { !it.startsWith("$") }.toList()
            val filtered = JSONObject()
            for (key in keys) {
                filtered.put(key, obj.get(key))
            }
            return JsonSerializable.sortJson(filtered.toString(), MetaExclusion.NONE)
        }

        /**
         * Remove both storage and client metadata from a JSON string.
         */
        fun omitMeta(json: String): String =
            JsonSerializable.sortJson(json, MetaExclusion.ALL_META)

        /**
         * Add hash metadata (_hash and _dataHash) to a payload's JSON.
         * Returns a new JSON string with the hash fields added.
         */
        fun addHashMeta(payload: Payload): String {
            val json = JsonSerializable.toJson(payload, MetaExclusion.NONE)
            val obj = JSONObject(json)
            obj.put("_hash", hashHex(payload))
            obj.put("_dataHash", dataHashHex(payload))
            return JsonSerializable.sortJson(obj.toString(), MetaExclusion.NONE)
        }

        /**
         * Add full storage metadata (_hash, _dataHash, _sequence) to a payload's JSON.
         * Returns a new JSON string with storage meta fields added.
         */
        fun addStorageMeta(payload: Payload, timestamp: Long = System.currentTimeMillis(), index: Int = 0): String {
            val json = JsonSerializable.toJson(payload, MetaExclusion.NONE)
            val obj = JSONObject(json)
            val hashVal = hashHex(payload)
            val dataHashVal = dataHashHex(payload)
            obj.put("_hash", hashVal)
            obj.put("_dataHash", dataHashVal)
            val hashBytes = hash(payload)
            obj.put("_sequence", Sequence.local(timestamp, index, hashBytes))
            return JsonSerializable.sortJson(obj.toString(), MetaExclusion.NONE)
        }
    }
}
