package network.xyo.client

class XyoBoundWitnessJson(): XyoBoundWitnessBodyJson(), XyoBoundWitnessMetaInterface {
    override var _signatures: Array<String>? = null
    override var _payloads: Array<XyoPayload>? = null
    override var _client: String? = null
    override var _hash: String? = null
}