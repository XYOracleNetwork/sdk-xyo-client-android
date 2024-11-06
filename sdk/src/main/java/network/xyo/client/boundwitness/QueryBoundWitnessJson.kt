package network.xyo.client.boundwitness

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class QueryBoundWitnessJson: XyoBoundWitnessJson(), XyoBoundWitnessMetaInterface {
    var query: String? = null

    // override to return a bound witness json body that has query in its hashable fields
    override fun getBodyJson(): QueryBoundWitnessBodyJson {
        return QueryBoundWitnessBodyJson(
            addresses,
            previous_hashes,
            payload_hashes,
            payload_schemas,
            query!!,
            timestamp,
        )
    }
}