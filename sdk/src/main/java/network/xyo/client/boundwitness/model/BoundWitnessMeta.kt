package network.xyo.client.boundwitness.model

import java.io.Serializable

interface BoundWitnessMeta: Serializable {
    var __signatures: List<String>
}