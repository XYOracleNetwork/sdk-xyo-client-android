package network.xyo.client.boundwitness

import network.xyo.client.payload.Payload

interface XyoBoundWitnessBodyInterface : Payload {
    var addresses: List<String>
    var payload_hashes: List<String>
    var payload_schemas: List<String>
    var previous_hashes: List<String?>
}