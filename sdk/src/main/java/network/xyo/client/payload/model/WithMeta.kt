package network.xyo.client.payload.model

import com.squareup.moshi.Json
import network.xyo.client.types.HashHex
import java.io.Serializable

interface WithMeta<T: Serializable> : Serializable {
    @Json(name = "\$meta")
    var _meta: T
}