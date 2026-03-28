package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.lib.JsonSerializable.Companion.MetaExclusion
import network.xyo.client.types.Hash

open class XyoException(message: String): Throwable(message)
open class XyoValidationException(message: String): XyoException(message)
open class XyoInvalidSchemaException(val schema: String): XyoValidationException("'schema' must be lowercase [${schema}]")

@JsonClass(generateAdapter = true)
open class Payload(override var schema: String) : network.xyo.client.payload.model.Payload, JsonSerializable() {
    @Throws(XyoValidationException::class)
    open fun validate() {
        if (schema != schema.lowercase()) {
            throw XyoInvalidSchemaException(schema)
        }
    }

    /**
     * Compute the dataHash: excludes both storage meta (`_` prefix) and client meta (`$` prefix).
     */
    open fun dataHash(): Hash {
        return sha256(this, MetaExclusion.ALL_META)
    }

    /**
     * Compute the hash: excludes storage meta (`_` prefix) but keeps client meta (`$` prefix).
     * Per the XYO Yellow Paper Section 3.2.
     */
    open fun hash(): Hash {
        return sha256(this, MetaExclusion.STORAGE_META)
    }
}