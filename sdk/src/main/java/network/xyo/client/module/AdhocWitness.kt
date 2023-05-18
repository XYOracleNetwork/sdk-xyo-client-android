package network.xyo.client.module

import network.xyo.payload.IPayload
import org.json.JSONArray

open class AdhocWitnessConfig(payloads: Set<IPayload>? = null, schema: String = "network.xyo.witness.adhoc.config"): WitnessConfig(schema) {
    init {
        val array = JSONArray()
        payloads?.forEach { payload -> array.put(payload) }
        this.put("payloads", array)
    }

    val payloads: Set<IPayload>
        get() {
            return this.getArrayAsObjectSet("payloads") as Set<IPayload>
        }
}

open class AdhocWitness(params: WitnessParams<AdhocWitnessConfig>) : Witness<AdhocWitnessConfig, WitnessParams<AdhocWitnessConfig>>(params) {
    override fun observe(payloads: Set<IPayload>?): Set<IPayload> {
        return payloads ?: this.params.config.payloads
    }
}