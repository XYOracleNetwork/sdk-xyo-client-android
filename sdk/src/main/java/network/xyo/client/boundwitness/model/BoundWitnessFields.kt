package network.xyo.client.boundwitness.model

import network.xyo.client.payload.model.Payload

interface BoundWitnessFields : Payload {
    var addresses: List<String>
    var payload_hashes: List<String>
    var payload_schemas: List<String>
    var previous_hashes: List<String?>
    // Note: Long is a higher precision type than JavaScript's Number type but it is the default type from
    // Kotlin's System.currentTimeMillis().
    var timestamp: Long?
}