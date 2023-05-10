package network.xyo.client.module

import android.content.Context
import network.xyo.client.payload.Payload

typealias AnyDiviner = Module<ModuleConfig, ModuleParams<ModuleConfig>>

open class Diviner<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
    open fun divine(context: Context, payloads: List<Payload>?): List<Payload> {
        return payloads ?: emptyList()
    }
}