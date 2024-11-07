package network.xyo.client.payload

import java.io.Serializable

interface Payload : Serializable {
    var schema: String
}