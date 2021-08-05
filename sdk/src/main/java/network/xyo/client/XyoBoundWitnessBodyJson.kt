package network.xyo.client

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class XyoBoundWitnessBodyJson(): XyoBoundWitnessBodyInterface, XyoSerializable() {

    final override var addresses = emptyList<String>()
    final override var previous_hashes = emptyList<String>()
    final override var payload_hashes = emptyList<String>()
    final override var payload_schemas = emptyList<String>()

    constructor (addresses: List<String>, previous_hashes: List<String>, payload_hashes: List<String>, payload_schemas: List<String>) : this() {
        this.addresses = addresses
        this.previous_hashes = previous_hashes
        this.payload_hashes = payload_hashes
        this.payload_schemas = payload_schemas
    }
}