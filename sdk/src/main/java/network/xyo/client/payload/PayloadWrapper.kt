package network.xyo.client.payload

import network.xyo.client.types.Hash
import network.xyo.client.types.HashHex

/**
 * Typed wrapper for Payload instances, matching JS PayloadWrapper/PayloadWrapperBase.
 *
 * Provides convenient accessors for hashing, validation, and schema access.
 */
open class PayloadWrapper<T : Payload>(val payload: T) {

    val schema: String
        get() = payload.schema

    fun dataHash(): Hash = PayloadHasher.dataHash(payload)

    fun dataHashHex(): HashHex = PayloadHasher.dataHashHex(payload)

    fun hash(): Hash = PayloadHasher.hash(payload)

    fun hashHex(): HashHex = PayloadHasher.hashHex(payload)

    open fun validate(): List<Error> {
        return PayloadValidator(payload).validate()
    }

    val isValid: Boolean
        get() = validate().isEmpty()

    companion object {
        fun <T : Payload> wrap(payload: T): PayloadWrapper<T> {
            return PayloadWrapper(payload)
        }

        fun <T : Payload> unwrap(wrapper: PayloadWrapper<T>): T {
            return wrapper.payload
        }

        fun <T : Payload> unwrap(wrappers: List<PayloadWrapper<T>>): List<T> {
            return wrappers.map { it.payload }
        }
    }
}
