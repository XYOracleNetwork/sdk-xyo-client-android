package network.xyo.client.payload

import java.io.Serializable

interface Payload : Serializable {
    val schema: String
}