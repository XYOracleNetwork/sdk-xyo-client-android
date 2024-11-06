package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.XyoSerializable

open class XyoException(message: String): Throwable(message)
open class XyoValidationException(message: String): XyoException(message)
open class XyoInvalidSchemaException(val schema: String): XyoValidationException("'schema' must be lowercase [${schema}]")

@JsonClass(generateAdapter = true)
open class XyoPayload(schema: String): Payload, XyoSerializable() {
    private val internalSchema = schema.lowercase()

    override val schema: String
        get() = this.internalSchema

    @Throws(XyoValidationException::class)
    open fun validate() {
        if (schema != schema.lowercase()) {
            throw XyoInvalidSchemaException(schema)
        }
    }
}