package network.xyo.client.payload.types

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

/**
 * Payload for schema definitions, matching JS @xyo-network/schema-payload-plugin.
 */
@JsonClass(generateAdapter = true)
open class SchemaPayload(
    val definition: Map<String, Any?>? = null
) : Payload(SCHEMA_VALUE) {
    companion object {
        const val SCHEMA_VALUE = "network.xyo.schema"
    }
}
