package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass
import network.xyo.client.boundwitness.model.BoundWitnessMeta
import network.xyo.client.lib.JsonSerializable

@JsonClass(generateAdapter = true)
class BoundWitnessMeta: BoundWitnessMeta, JsonSerializable() {
    override var signatures: List<String>? = null
    override var client: String? = null
}