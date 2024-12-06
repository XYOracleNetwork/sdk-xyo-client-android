package network.xyo.client.payload

import com.squareup.moshi.Json
import java.io.Serializable

interface Payload : Serializable {
    var schema: String
}

interface PayloadWithMeta : Serializable {
    @Json(name = "\$meta")
    var _meta: Serializable
}