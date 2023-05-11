package network.xyo.client.module

import network.xyo.client.payload.Payload

typealias AnyWitness = Witness<*, *>

open class Witness<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
    open fun observe(payloads: Set<Payload> = emptySet()): Set<Payload> {
        return payloads
    }
}