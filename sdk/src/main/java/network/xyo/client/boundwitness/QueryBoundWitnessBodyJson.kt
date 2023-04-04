package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class QueryBoundWitnessBodyJson(): XyoBoundWitnessBodyJson() {
    var query: String? = null

    constructor (addresses: List<String>, previous_hashes: List<String>, payload_hashes: List<String>, payload_schemas: List<String>, query: String) : this() {
        this.query = query
    }
}