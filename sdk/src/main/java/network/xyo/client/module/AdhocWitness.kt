package network.xyo.client.module

import network.xyo.payload.Payload
import org.json.JSONArray

class AdhocWitnessConfig(payloads: JSONArray? = null, schema: String = "network.xyo.witness.adhoc.config"): ModuleConfig(schema) {
    init {
        this.put("payloads", payloads)
    }
}

open class AdhocWitness(params: ModuleParams<AdhocWitnessConfig>) : Witness<AdhocWitnessConfig, ModuleParams<AdhocWitnessConfig>>(params) {
    override fun observe(payloads: JSONArray?): JSONArray {
        return payloads ?: this.params.config.getJSONArray("payloads")
    }
}