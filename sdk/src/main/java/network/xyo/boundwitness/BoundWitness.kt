package network.xyo.boundwitness

import network.xyo.payload.IPayload
import network.xyo.payload.JSONPayload
import org.json.JSONArray

interface IBoundWitness: IPayload {
    var addresses: List<String>
    var _signatures: List<String>
    var payload_hashes: List<String>
    var payload_schemas: List<String>
    var previous_hashes: List<String?>
}
open class JSONBoundWitness(schema: String = Companion.schema): JSONPayload(schema), IBoundWitness {
    override var addresses: List<String>
        get() {
            return getArrayAsStringList("addresses")
        }

        set(value) {
            this.put("addresses", JSONArray(value) )
        }

    override var _signatures: List<String>
        get() {
            return getArrayAsStringList("_signatures")
        }

        set(value) {
            this.put("_signatures", JSONArray(value) )
        }

    override var payload_hashes: List<String>
        get() {
            return getArrayAsStringList("payload_hashes")
        }

        set(value) {
            this.put("payload_hashes", JSONArray(value) )
        }

    override var payload_schemas: List<String>
        get() {
            return getArrayAsStringList("payload_schemas")
        }

        set(value) {
            this.put("payload_schemas", JSONArray(value) )
        }

    override var previous_hashes: List<String?>
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