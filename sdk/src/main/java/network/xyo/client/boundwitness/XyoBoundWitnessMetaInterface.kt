package network.xyo.client.boundwitness

interface XyoBoundWitnessMetaInterface {
    var _hash: String?
    var _signatures: List<String>?
    var _payloads: List<*>?
    var _client: String?
    var _previous_hash: String?
}