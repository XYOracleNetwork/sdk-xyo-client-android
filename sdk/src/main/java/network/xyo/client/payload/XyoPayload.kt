package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.XyoSerializable

open class XyoException(message: String): Throwable(message)
open class XyoValidationException(message: String): XyoException(message)
open class XyoInvalidSchemaException(val schema: String): XyoValidationException("'schema' must be lowercase [${schema}]")
open class XyoInvalidPreviousHashException(val previousHash: String?): XyoValidationException("'previous_hash' must be lowercase [${previousHash}]")

@JsonClass(generateAdapter = true)
open class XyoPayload(schema: String, previousHash: String? = null): XyoSerializable() {
    var schema = schema.lowercase()
    var previousHash = previousHash?.lowercase()
    @Throws(XyoValidationException::class)
    open fun validate() {
        if (schema != schema.lowercase()) {
            throw XyoInvalidSchemaException(schema)
        }
        if (previousHash != previousHash?.lowercase()) {
            throw XyoInvalidPreviousHashException(previousHash)
        }
    }
}