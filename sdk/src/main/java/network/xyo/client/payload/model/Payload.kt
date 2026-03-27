package network.xyo.client.payload.model

import java.io.Serializable

interface Payload : Serializable {
    val schema: String
}
