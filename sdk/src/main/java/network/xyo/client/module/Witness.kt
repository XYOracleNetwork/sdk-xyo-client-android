package network.xyo.client.module

import org.json.JSONArray

typealias AnyWitness = Witness<*, *>

open class Witness<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
    open fun observe(payloads: JSONArray? = null): JSONArray {
        return payloads ?: JSONArray()
    }
}