package network.xyo.client

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class XyoBoundWitnessJson: XyoBoundWitnessBodyJson(), XyoBoundWitnessMetaInterface {
    override var _signatures: List<String>? = null
    override var _payloads: List<*>? = null
    override var _client: String? = null
    override var _hash: String? = null
}