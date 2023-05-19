package network.xyo.witness

import network.xyo.resolver.CompositeModuleResolver
import network.xyo.account.Account
import network.xyo.module.AbstractModule
import network.xyo.module.IModule
import network.xyo.module.ModuleConfig
import network.xyo.module.ModuleParams
import network.xyo.module.ModuleResolver
import network.xyo.payload.IPayload

open class WitnessConfig(schema: String = ModuleConfig.schema): ModuleConfig(schema) {
    companion object {
        const val schema = "network.xyo.witness.config"
    }
}
open class WitnessParams<TConfig: WitnessConfig>(account: Account, config: TConfig): ModuleParams<TConfig>(account, config)

interface IWitness<TConfig: WitnessConfig, TParams : WitnessParams<TConfig>, TResolver : ModuleResolver> :
    IModule<TConfig, TParams, TResolver> {
    fun observe(payloads: Set<IPayload>? = null): Set<IPayload>
}

typealias AnyWitness = IWitness<*, *, *>

open class Witness<TConfig: WitnessConfig, TParams : WitnessParams<TConfig>>(
    params: TParams
) : IWitness<TConfig, TParams, CompositeModuleResolver>, AbstractModule<TConfig, TParams>(params) {
    override fun observe(payloads: Set<IPayload>?): Set<IPayload> {
        return payloads ?: emptySet()
    }
}