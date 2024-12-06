package network.xyo.client.payload.model

import com.squareup.moshi.Json
import java.io.Serializable

interface Payload : Serializable {
    var schema: String
}

interface WithMeta : Serializable {
    @Json(name = "\$meta")
    var _meta: Serializable
}