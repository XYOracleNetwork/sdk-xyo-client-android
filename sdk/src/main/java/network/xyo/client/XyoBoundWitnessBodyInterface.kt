package network.xyo.client

interface XyoBoundWitnessBodyInterface {
    var addresses: Array<String>
    var previous_hashes: Array<String?>
    var payload_hashes: Array<String>
    var payload_schemas: Array<String>
}