package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass
import network.xyo.client.boundwitness.model.BoundWitnessBody
import network.xyo.client.payload.Payload

@JsonClass(generateAdapter = true)
open class BoundWitnessBody(): BoundWitnessBody, Payload(SCHEMA) {
    final override var addresses = emptyList<String>()
    final override var payload_hashes = emptyList<String>()
    final override var payload_schemas = emptyList<String>()
    final override var previous_hashes = emptyList<String?>()
    final override var timestamp: Long? = null

    override fun dataHash(): String {
        return sha256String(this)
    }

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
    }

    companion object {
        const val SCHEMA = "network.xyo.boundwitness"
    }
}