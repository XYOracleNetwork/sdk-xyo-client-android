package network.xyo.client.boundwitness

import network.xyo.client.payload.Payload

interface XyoBoundWitnessMetaInterface : Payload {
    var _hash: String?
    var _signatures: List<String>?
    var _payloads: List<*>?
    var _client: String?
    var _previous_hash: String?
}