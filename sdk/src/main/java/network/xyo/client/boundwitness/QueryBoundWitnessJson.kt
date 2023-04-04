package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class QueryBoundWitnessJson: XyoBoundWitnessJson(), XyoBoundWitnessMetaInterface {
    var query: String? = null
}