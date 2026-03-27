package network.xyo.client.boundwitness.model

import network.xyo.client.payload.model.Payload

interface BoundWitnessFields : Payload {
    val addresses: List<String>
    val payload_hashes: List<String>
    val payload_schemas: List<String>
    val previous_hashes: List<String?>
    val timestamp: Long?
}
