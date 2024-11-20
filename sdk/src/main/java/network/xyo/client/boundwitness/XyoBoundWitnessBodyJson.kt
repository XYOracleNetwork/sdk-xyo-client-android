package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload

@JsonClass(generateAdapter = true)
open class XyoBoundWitnessBodyJson(): XyoBoundWitnessBodyInterface, XyoPayload() {
    override var schema: String
        get() = "network.xyo.boundwitness"
        set(value) = Unit
    final override var addresses = emptyList<String>()
    final override var payload_hashes = emptyList<String>()
    final override var payload_schemas = emptyList<String>()
    final override var previous_hashes = emptyList<String?>()
    final override var timestamp: Long = System.currentTimeMillis()

    override fun hash(): String {
        return sha256String(this)
    }

    constructor (addresses: List<String>, previous_hashes: List<String?>, payload_hashes: List<String>, payload_schemas: List<String>, timestamp: Long) : this() {
        this.addresses = addresses
        this.previous_hashes = previous_hashes
        this.payload_hashes = payload_hashes
        this.payload_schemas = payload_schemas
        this.timestamp = timestamp
    }
}