package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class QueryBoundWitnessFields(
    addresses: List<String>,
    previous_hashes: List<String?>,
    payload_hashes: List<String>,
    payload_schemas: List<String>,
    val query: String,
    timestamp: Long?,
): BoundWitnessFields(addresses, previous_hashes, payload_hashes, payload_schemas, timestamp)