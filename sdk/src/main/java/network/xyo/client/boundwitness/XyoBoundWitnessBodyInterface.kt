package network.xyo.client.boundwitness

interface XyoBoundWitnessBodyInterface {
    var addresses: List<String>
    var payload_hashes: List<String>
    var payload_schemas: List<String>
    var previous_hashes: List<String>
    var schema: String
}