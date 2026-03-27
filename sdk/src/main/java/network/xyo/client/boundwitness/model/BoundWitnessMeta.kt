package network.xyo.client.boundwitness.model

import java.io.Serializable

interface BoundWitnessMeta : Serializable {
    val __signatures: List<String>
}
