package network.xyo.client

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
interface XyoBoundWitnessBodyInterface {
    var addresses: List<String>
    var previous_hashes: List<String>
    var payload_hashes: List<String>
    var payload_schemas: List<String>
}