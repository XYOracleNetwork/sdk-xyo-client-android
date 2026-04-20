package network.xyo.chain.protocol.validation

import network.xyo.client.boundwitness.BoundWitnessValidator
import network.xyo.client.boundwitness.model.BoundWitnessFields
import network.xyo.client.boundwitness.model.BoundWitnessMeta

private fun sdkErrorCode(message: String): String {
    return when {
        message.startsWith("schema must be") -> "INVALID_SCHEMA"
        message.startsWith("addresses must be unique") -> "DUPLICATE_ADDRESSES"
        message.startsWith("addresses must not be empty") -> "MISSING_ADDRESSES"
        message.startsWith("previous_hashes length") -> "PREVIOUS_HASH_MISMATCH"
        message.startsWith("payload_hashes length") -> "PAYLOAD_MISMATCH"
        message.startsWith("payload_schemas[") -> "INVALID_PAYLOAD_SCHEMA"
        message.startsWith("signatures length") -> "SIGNATURE_MISMATCH"
        message.startsWith("no address at index") -> "MISSING_SIGNATURE_ADDRESS"
        message.startsWith("could not recover public key") -> "INVALID_SIGNATURE"
        message.startsWith("signature address mismatch") -> "SIGNATURE_ADDRESS_MISMATCH"
        message.startsWith("signature validation failed") -> "INVALID_SIGNATURE"
        else -> "BOUND_WITNESS_INVALID"
    }
}

internal fun validateSdkBoundWitness(
    fields: BoundWitnessFields,
    meta: BoundWitnessMeta,
): List<ValidationError> {
    val validator = BoundWitnessValidator(fields, meta)
    val sdkErrors = buildList {
        addAll(validator.schema())
        addAll(validator.addresses())
        addAll(validator.previousHashes())
        addAll(validator.validatePayloadHashesLength())
        addAll(validator.schemas())

        // Protocol validation is used for both pre-sign and post-sign witnesses.
        // Only enforce signature-related structural checks when signatures exist.
        if (meta.__signatures.isNotEmpty()) {
            addAll(validator.signatures())
        }
    }

    val protocolErrors = sdkErrors.map {
        ValidationError(sdkErrorCode(it.message.orEmpty()), it.message.orEmpty())
    }.toMutableList()

    if (meta.__signatures.isNotEmpty() && meta.__signatures.size != fields.addresses.size) {
        protocolErrors.add(
            ValidationError(
                "SIGNATURE_MISMATCH",
                "signatures length (${meta.__signatures.size}) must match addresses length (${fields.addresses.size})",
            )
        )
    }

    return protocolErrors
}
