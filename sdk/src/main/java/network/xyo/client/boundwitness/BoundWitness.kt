package network.xyo.client.boundwitness

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.model.WithMeta
import network.xyo.client.types.HashHex

@JsonClass(generateAdapter = true)
open class BoundWitness: WithMeta<BoundWitnessMeta>, BoundWitnessBody() {
    @Json(name = "\$meta")
    override var _meta = BoundWitnessMeta()

    @Json(name = "\$hash")
    override var _hash: HashHex
        get() {
            return this.dataHash()
        }
        set(value){}

    override fun dataHash(): String {
        return getBodyJson().dataHash()
    }

    fun rootHash(): String {
        return sha256String(this)
    }

    open fun getBodyJson(): BoundWitnessBody {
        return BoundWitnessBody(this.addresses, this.previous_hashes, this.payload_hashes, this.payload_schemas, this.timestamp)
    }
}