package network.xyo.client.module

import network.xyo.client.payload.Payload

open class AdhocWitness(params: ModuleParams<ModuleConfig>) : Witness<ModuleConfig, ModuleParams<ModuleConfig>>(params) {
    override fun observe(payloads: Set<Payload>): Set<Payload> {
        return payloads
    }
}