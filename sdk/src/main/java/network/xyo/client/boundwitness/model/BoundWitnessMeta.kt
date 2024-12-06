package network.xyo.client.boundwitness.model

import java.io.Serializable

interface BoundWitnessMeta: Serializable {
    var signatures: List<String>?
    var client: String?
}