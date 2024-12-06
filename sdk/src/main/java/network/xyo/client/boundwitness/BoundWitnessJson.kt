package network.xyo.client.boundwitness

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.client.lib.JsonSerializable

@JsonClass(generateAdapter = true)
class XyoBoundWitnessMeta: XyoBoundWitnessMetaInterface, JsonSerializable() {
    override var signatures: List<String>? = null
    override var client: String? = null
}

@JsonClass(generateAdapter = true)
open class BoundWitnessJson: BoundWitnessBodyJson() {
    @Json(ignore = true)
    val _meta: XyoBoundWitnessMeta = XyoBoundWitnessMeta()

    @Json(name = "\$meta")
    var meta: XyoBoundWitnessMeta
        get() = _meta
        set(value) = Unit

    override fun dataHash(): String {
        return getBodyJson().dataHash()
    }

    fun rootHash(): String {
        return sha256String(this)
    }

    open fun getBodyJson(): BoundWitnessBodyJson {
        return BoundWitnessBodyJson(this.addresses, this.previous_hashes, this.payload_hashes, this.payload_schemas, this.timestamp)
    }
}