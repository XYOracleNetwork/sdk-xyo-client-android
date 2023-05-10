package network.xyo.client.boundwitness

import network.xyo.client.payload.Payload
import org.json.JSONArray

open class BoundWitness(schema: String = BoundWitness.schema): Payload(schema) {
    var addresses: List<String>
        get() {
            return getArrayAsStringList("addresses")
        }

        set(value) {
            this.put("addresses", JSONArray(value) )
        }

    var _signatures: List<String>
        get() {
            return getArrayAsStringList("_signatures")
        }

        set(value) {
            this.put("_signatures", JSONArray(value) )
        }

    var payload_hashes: List<String>
        get() {
            return getArrayAsStringList("payload_hashes")
        }

        set(value) {
            this.put("payload_hashes", JSONArray(value) )
        }

    var payload_schemas: List<String>
        get() {
            return getArrayAsStringList("payload_schemas")
        }

        set(value) {
            this.put("payload_schemas", JSONArray(value) )
        }

    var previous_hashes: List<String?>
        get() {
            return getArrayAsStringList("previous_hashes")
        }

        set(value) {
            this.put("previous_hashes", JSONArray(value) )
        }

    companion object {
        const val schema = "network.xyo.boundwitness"
    }
}