package network.xyo.client.boundwitness

import network.xyo.client.boundwitness.model.BoundWitnessFields as BoundWitnessFieldsModel
import network.xyo.client.boundwitness.model.BoundWitnessMeta
import network.xyo.client.lib.hexStringToByteArray
import network.xyo.client.lib.publicKeyToAddress
import network.xyo.client.lib.recoverPublicKey
import network.xyo.client.payload.PayloadHasher
import network.xyo.client.payload.PayloadValidator

/**
 * Validates any object implementing BoundWitnessFields + BoundWitnessMeta.
 *
 * Works with sdk's BoundWitness, protocol's BlockBoundWitness,
 * TransactionBoundWitness, or any other implementation.
 *
 * Validates:
 * - Schema correctness
 * - Address uniqueness
 * - Array length consistency
 * - Signature validity (when dataHash is provided)
 */
class BoundWitnessValidator(
    private val fields: BoundWitnessFieldsModel,
    private val meta: BoundWitnessMeta,
    private val dataHash: ByteArray? = null,
) {

    /**
     * Convenience constructor for objects that implement both interfaces.
     */
    constructor(bw: BoundWitness) : this(
        fields = bw,
        meta = bw,
        dataHash = bw.dataHash(),
    )

    /**
     * Validate that the schema is the BoundWitness schema.
     */
    fun schema(): List<Error> {
        val errors = mutableListOf<Error>()
        if (fields.schema != BOUND_WITNESS_SCHEMA) {
            errors.add(Error("schema must be '$BOUND_WITNESS_SCHEMA', got '${fields.schema}'"))
        }
        return errors
    }

    /**
     * Validate that addresses are unique.
     */
    fun addressesUniqueness(): List<Error> {
        val errors = mutableListOf<Error>()
        val addresses = fields.addresses
        if (addresses.size != addresses.toSet().size) {
            errors.add(Error("addresses must be unique"))
        }
        return errors
    }

    /**
     * Validate addresses are present.
     */
    fun addresses(): List<Error> {
        val errors = mutableListOf<Error>()
        errors.addAll(addressesUniqueness())
        if (fields.addresses.isEmpty()) {
            errors.add(Error("addresses must not be empty"))
        }
        return errors
    }

    /**
     * Validate previous_hashes array length matches addresses.
     */
    fun previousHashes(): List<Error> {
        val errors = mutableListOf<Error>()
        if (fields.previous_hashes.size != fields.addresses.size) {
            errors.add(Error("previous_hashes length (${fields.previous_hashes.size}) must match addresses length (${fields.addresses.size})"))
        }
        return errors
    }

    /**
     * Validate payload_hashes length matches payload_schemas length.
     */
    fun validatePayloadHashesLength(): List<Error> {
        val errors = mutableListOf<Error>()
        if (fields.payload_hashes.size != fields.payload_schemas.size) {
            errors.add(Error("payload_hashes length (${fields.payload_hashes.size}) must match payload_schemas length (${fields.payload_schemas.size})"))
        }
        return errors
    }

    /**
     * Validate that all array lengths are consistent.
     */
    fun validateArrayLengths(): List<Error> {
        val errors = mutableListOf<Error>()
        errors.addAll(previousHashes())
        errors.addAll(validatePayloadHashesLength())

        val sigCount = meta.__signatures.size
        val addrCount = fields.addresses.size
        if (sigCount != addrCount) {
            errors.add(Error("signatures length ($sigCount) must match addresses length ($addrCount)"))
        }
        return errors
    }

    /**
     * Validate all payload schemas are non-empty.
     */
    fun schemas(): List<Error> {
        val errors = mutableListOf<Error>()
        fields.payload_schemas.forEachIndexed { index, schema ->
            if (schema.isBlank()) {
                errors.add(Error("payload_schemas[$index] must not be blank"))
            }
        }
        return errors
    }

    /**
     * Validate all signatures against addresses using the data hash.
     * Skipped if no data hash was provided.
     */
    fun signatures(): List<Error> {
        val hash = dataHash ?: return emptyList()
        val errors = mutableListOf<Error>()

        meta.__signatures.forEachIndexed { index, signatureHex ->
            val expectedAddress = fields.addresses.getOrNull(index)
            if (expectedAddress == null) {
                errors.add(Error("no address at index $index for signature"))
                return@forEachIndexed
            }

            val signatureErrors = validateSignature(hash, expectedAddress, signatureHex)
            errors.addAll(signatureErrors)
        }
        return errors
    }

    /**
     * Run all validations.
     */
    fun validate(): List<Error> {
        val errors = mutableListOf<Error>()
        errors.addAll(schema())
        errors.addAll(addresses())
        errors.addAll(validateArrayLengths())
        errors.addAll(schemas())
        errors.addAll(signatures())
        return errors
    }

    companion object {
        private const val BOUND_WITNESS_SCHEMA = "network.xyo.boundwitness"

        /**
         * Validate a single signature against an expected address.
         */
        fun validateSignature(hash: ByteArray, expectedAddressHex: String, signatureHex: String): List<Error> {
            val errors = mutableListOf<Error>()
            try {
                val signature = hexStringToByteArray(signatureHex)
                val recoveredPublicKey = recoverPublicKey(hash, signature)
                if (recoveredPublicKey == null) {
                    errors.add(Error("could not recover public key from signature"))
                    return errors
                }
                val recoveredAddress = publicKeyToAddress(recoveredPublicKey)
                val recoveredAddressHex = PayloadHasher.bytesToHex(recoveredAddress)
                if (recoveredAddressHex != expectedAddressHex) {
                    errors.add(Error("signature address mismatch: expected $expectedAddressHex, got $recoveredAddressHex"))
                }
            } catch (e: Exception) {
                errors.add(Error("signature validation failed: ${e.message}"))
            }
            return errors
        }
    }
}
