package network.xyo.client

open class XyoBoundWitnessBodyJson(): XyoBoundWitnessBodyInterface {

    final override var addresses = emptyArray<String>()
    final override var previous_hashes = emptyArray<String?>()
    final override var payload_hashes = emptyArray<String>()
    final override var payload_schemas = emptyArray<String>()

    constructor (addresses: Array<String>, previous_hashes: Array<String?>, payload_hashes: Array<String>, payload_schemas: Array<String>) : this() {
        this.addresses = addresses
        this.previous_hashes = previous_hashes
        this.payload_hashes = payload_hashes
        this.payload_schemas = payload_schemas
    }
}