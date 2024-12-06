package network.xyo.client.payload

import com.squareup.moshi.JsonClass
import network.xyo.client.lib.JsonSerializable

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

    open fun dataHash(): String {
        return sha256String(this)
    }
}