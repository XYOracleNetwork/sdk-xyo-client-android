package network.xyo.client.module

import android.content.Context
import network.xyo.client.payload.Payload

typealias AnyWitness = Module<ModuleConfig, ModuleParams<ModuleConfig>>

open class Witness<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
    open fun observe(payloads: Set<Payload> = emptySet()): Set<Payload> {
        return payloads
    }
}