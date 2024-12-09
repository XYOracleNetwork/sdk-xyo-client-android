package network.xyo.client.boundwitness

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.model.WithMeta
import network.xyo.client.types.Hash
import network.xyo.client.types.HashHex

@JsonClass(generateAdapter = true)
open class BoundWitness: WithMeta<BoundWitnessMeta>, BoundWitnessFields() {
    @Json(name = "\$meta")
    override var _meta = BoundWitnessMeta()
}