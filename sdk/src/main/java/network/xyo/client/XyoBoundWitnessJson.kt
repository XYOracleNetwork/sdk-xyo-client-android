package network.xyo.client

class XyoBoundWitnessJson: XyoBoundWitnessBodyJson(), XyoBoundWitnessMetaInterface {
    override var _signatures: List<String>? = null
    override var _payloads: List<XyoPayload>? = null
    override var _client: String? = null
    override var _hash: String? = null
}