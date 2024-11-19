package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class XyoBoundWitnessJson: XyoBoundWitnessBodyJson(), XyoBoundWitnessMetaInterface {
    override var _signatures: List<String>? = null
    override var _client: String? = null
    override var _hash: String? = null

    open fun getBodyJson(): XyoBoundWitnessBodyJson {
        return this
    }
}