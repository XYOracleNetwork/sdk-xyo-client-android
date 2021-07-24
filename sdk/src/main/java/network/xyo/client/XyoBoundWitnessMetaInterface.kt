package network.xyo.client

interface XyoBoundWitnessMetaInterface {
    var _hash: String?
    var _signatures: List<String>?
    var _payloads: List<XyoPayload>?
    var _client: String?
}