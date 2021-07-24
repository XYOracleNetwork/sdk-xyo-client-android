package network.xyo.client

interface XyoBoundWitnessBodyInterface {
    var addresses: List<String>
    var previous_hashes: List<String?>
    var payload_hashes: List<String>
    var payload_schemas: List<String>
}