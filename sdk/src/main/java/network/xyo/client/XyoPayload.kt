package network.xyo.client

import com.squareup.moshi.JsonClass

open class XyoException(message: String): Throwable(message)
open class XyoValidationException(message: String): XyoException(message)
open class XyoInvalidSchemaException(val schema: String): XyoValidationException("'schema' must be lowercase [${schema}]")

@JsonClass(generateAdapter = true)
open class XyoPayload(var schema: String, var previousHash: String? = null): XyoSerializable() {
    @Throws(XyoValidationException::class)
    open fun validate() {
        if (schema != schema.lowercase()) {
            throw XyoInvalidSchemaException(schema)
        }
    }
}