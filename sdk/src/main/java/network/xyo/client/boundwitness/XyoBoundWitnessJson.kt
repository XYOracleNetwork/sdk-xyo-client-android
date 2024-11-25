package network.xyo.client.boundwitness

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class XyoBoundWitnessMeta: XyoBoundWitnessMetaInterface {
    override var signatures: List<String>? = null
    override var client: String? = null
}

@JsonClass(generateAdapter = true)
open class XyoBoundWitnessJson: XyoBoundWitnessBodyJson() {
    val _meta: XyoBoundWitnessMeta = XyoBoundWitnessMeta()

    @Json(name = "\$meta")
    var meta: XyoBoundWitnessMeta
        get() = _meta
        set(value) = Unit

    override fun hash(): String {
        return getBodyJson().hash()
    }

    fun rootHash(): String {
        return sha256String(this)
    }

    open fun getBodyJson(): XyoBoundWitnessBodyJson {
        return XyoBoundWitnessBodyJson(this.addresses, this.previous_hashes, this.payload_hashes, this.payload_schemas, this.timestamp)
    }
}