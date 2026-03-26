package network.xyo.client.boundwitness

import network.xyo.client.lib.hexStringToByteArray
import network.xyo.client.lib.publicKeyToAddress
import network.xyo.client.lib.recoverPublicKey
import network.xyo.client.payload.PayloadHasher
import network.xyo.client.payload.PayloadValidator

/**
 * Validates BoundWitness instances, matching JS BoundWitnessValidator.
 *
 * Validates:
 * - Schema correctness
 * - Address uniqueness
 * - Array length consistency (addresses, payload_hashes, payload_schemas, previous_hashes, signatures)
 * - Signature validity
 */
class BoundWitnessValidator<T : BoundWitness>(payload: T) : PayloadValidator<T>(payload) {

    /**
     * Validate that the schema is the BoundWitness schema.
     */
    fun schema(): List<Error> {
        val errors = mutableListOf<Error>()
        if (payload.schema != BoundWitnessFields.SCHEMA) {
            errors.add(Error("schema must be '${BoundWitnessFields.SCHEMA}', got '${payload.schema}'"))
        }
        return errors
    }

    /**
     * Validate that addresses are unique.
     */
    fun addressesUniqueness(): List<Error> {
        val errors = mutableListOf<Error>()
        val addresses = payload.addresses
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
        if (payload.addresses.isEmpty()) {
            errors.add(Error("addresses must not be empty"))
        }
        return errors
    }

    /**
     * Validate previous_hashes array length matches addresses.
     */
    fun previousHashes(): List<Error> {
        val errors = mutableListOf<Error>()
        if (payload.previous_hashes.size != payload.addresses.size) {
            errors.add(Error("previous_hashes length (${payload.previous_hashes.size}) must match addresses length (${payload.addresses.size})"))
        }
        return errors
    }

    /**
     * Validate payload_hashes length matches payload_schemas length.
     */
    fun validatePayloadHashesLength(): List<Error> {
        val errors = mutableListOf<Error>()
        if (payload.payload_hashes.size != payload.payload_schemas.size) {
            errors.add(Error("payload_hashes length (${payload.payload_hashes.size}) must match payload_schemas length (${payload.payload_schemas.size})"))
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

        val sigCount = payload.__signatures.size
        val addrCount = payload.addresses.size
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
        payload.payload_schemas.forEachIndexed { index, schema ->
            if (schema.isBlank()) {
                errors.add(Error("payload_schemas[$index] must not be blank"))
            }
        }
        return errors
    }

    /**
     * Validate all signatures against addresses using the data hash.
     */
    fun signatures(): List<Error> {
        val errors = mutableListOf<Error>()
        val dataHash = payload.dataHash()

        payload.__signatures.forEachIndexed { index, signatureHex ->
            val expectedAddress = payload.addresses.getOrNull(index)
            if (expectedAddress == null) {
                errors.add(Error("no address at index $index for signature"))
                return@forEachIndexed
            }

            val signatureErrors = validateSignature(dataHash, expectedAddress, signatureHex)
            errors.addAll(signatureErrors)
        }
        return errors
    }

    /**
     * Run all validations.
     */
    override fun validate(): List<Error> {
        val errors = mutableListOf<Error>()
        errors.addAll(schema())
        errors.addAll(addresses())
        errors.addAll(validateArrayLengths())
        errors.addAll(schemas())
        errors.addAll(signatures())
        return errors
    }

    companion object {
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
