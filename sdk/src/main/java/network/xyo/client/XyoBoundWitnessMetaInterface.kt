package network.xyo.client

interface XyoBoundWitnessMetaInterface {
    var _hash: String?
    var _signatures: Array<String>?
    var _payloads: Array<XyoPayload>?
    var _client: String?
}