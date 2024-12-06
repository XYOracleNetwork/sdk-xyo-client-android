package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class QueryBoundWitness: BoundWitness() {
    var query: String? = null

    // override to return a bound witness json body that has query in its hashable fields
    override fun getBodyJson(): QueryBoundWitnessBody {
        return QueryBoundWitnessBody(
            addresses,
            previous_hashes,
            payload_hashes,
            payload_schemas,
            query!!,
            timestamp,
        )
    }
}