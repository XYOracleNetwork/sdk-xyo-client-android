package network.xyo.client.boundwitness

import network.xyo.client.payload.Payload
import network.xyo.client.payload.PayloadHasher
import network.xyo.client.payload.PayloadWrapper
import network.xyo.client.types.Hash
import network.xyo.client.types.HashHex

/**
 * Typed wrapper for BoundWitness instances, matching JS BoundWitnessWrapper.
 *
 * Provides convenient accessors for addresses, payload hashes, schemas,
 * previous hashes, and payload lookup methods.
 */
open class BoundWitnessWrapper<T : BoundWitness>(
    val boundwitness: T,
    val payloads: List<Payload> = emptyList()
) : PayloadWrapper<T>(boundwitness) {

    val addresses: List<String>
        get() = boundwitness.addresses

    val payloadHashes: List<String>
        get() = boundwitness.payload_hashes

    val payloadSchemas: List<String>
        get() = boundwitness.payload_schemas

    val previousHashes: List<String?>
        get() = boundwitness.previous_hashes

    val signatures: List<String>
        get() = boundwitness.__signatures

    val timestamp: Long?
        get() = boundwitness.timestamp

    /**
     * Get payload hashes that match a given schema.
     */
    fun hashesBySchema(schema: String): List<String> {
        return boundwitness.payload_schemas.mapIndexedNotNull { index, s ->
            if (s == schema) boundwitness.payload_hashes.getOrNull(index) else null
        }
    }

    /**
     * Get payloads matching a given schema.
     */
    fun <P : Payload> payloadsBySchema(schema: String): List<P> {
        val matchingHashes = hashesBySchema(schema).toSet()
        @Suppress("UNCHECKED_CAST")
        return payloads.filter { payload ->
            PayloadHasher.hashHex(payload) in matchingHashes ||
            PayloadHasher.dataHashHex(payload) in matchingHashes
        } as List<P>
    }

    /**
     * Get payloads matching the given data hashes.
     */
    fun payloadsByDataHashes(hashes: List<HashHex>): List<Payload> {
        val hashSet = hashes.toSet()
        return payloads.filter { PayloadHasher.dataHashHex(it) in hashSet }
    }

    /**
     * Get payloads matching the given full hashes.
     */
    fun payloadsByHashes(hashes: List<HashHex>): List<Payload> {
        val hashSet = hashes.toSet()
        return payloads.filter { PayloadHasher.hashHex(it) in hashSet }
    }

    /**
     * Get the previous hash for a specific address.
     */
    fun previousHashForAddress(address: String): String? {
        val index = boundwitness.addresses.indexOf(address)
        if (index < 0) return null
        return boundwitness.previous_hashes.getOrNull(index)
    }

    /**
     * Get payload hashes that are not found in the provided payloads list.
     */
    fun missingPayloads(): List<String> {
        val availableHashes = payloads.map { PayloadHasher.hashHex(it) }.toSet() +
            payloads.map { PayloadHasher.dataHashHex(it) }.toSet()
        return boundwitness.payload_hashes.filter { it !in availableHashes }
    }

    /**
     * Get wrapped versions of all payloads.
     */
    fun wrappedPayloads(): List<PayloadWrapper<Payload>> {
        return payloads.map { PayloadWrapper.wrap(it) }
    }

    /**
     * Return as a result tuple matching JS toResult().
     */
    fun toResult(): Pair<T, List<Payload>> {
        return Pair(boundwitness, payloads)
    }

    override fun validate(): List<Error> {
        return BoundWitnessValidator(boundwitness).validate()
    }

    companion object {
        fun <T : BoundWitness> wrap(
            boundwitness: T,
            payloads: List<Payload> = emptyList()
        ): BoundWitnessWrapper<T> {
            return BoundWitnessWrapper(boundwitness, payloads)
        }
    }
}
