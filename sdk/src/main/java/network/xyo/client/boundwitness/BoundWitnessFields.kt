package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass
import network.xyo.client.boundwitness.model.BoundWitnessFields
import network.xyo.client.payload.Payload

@JsonClass(generateAdapter = true)
open class BoundWitnessFields(): BoundWitnessFields, Payload(SCHEMA) {
    final override var addresses = emptyList<String>()
    final override var payload_hashes = emptyList<String>()
    final override var payload_schemas = emptyList<String>()
    final override var previous_hashes = emptyList<String?>()
    final override var timestamp: Long? = null

    constructor (
        addresses: List<String>,
        previous_hashes: List<String?>,
        payload_hashes: List<String>,
        payload_schemas: List<String>,
        timestamp: Long? = null,
    ) : this() {
        this.addresses = addresses
        this.previous_hashes = previous_hashes
        this.payload_hashes = payload_hashes
        this.payload_schemas = payload_schemas
        this.timestamp = timestamp
    }

    companion object {
        const val SCHEMA = "network.xyo.boundwitness"
    }
}