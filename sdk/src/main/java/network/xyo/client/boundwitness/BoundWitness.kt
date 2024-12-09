package network.xyo.client.boundwitness

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import network.xyo.client.boundwitness.model.BoundWitnessMeta

@JsonClass(generateAdapter = true)
open class BoundWitness(client: String? = "android", signatures: List<String> = emptyList()) : BoundWitnessMeta,
    BoundWitnessFields() {
    @Json(name = "\$client")
    final override var __client: String? = client

    @Json(name = "\$signatures")
    final override var __signatures = signatures
}