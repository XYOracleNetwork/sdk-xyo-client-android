package network.xyo.client.module

import network.xyo.client.CompositeModuleResolver
import network.xyo.client.address.Account
import network.xyo.payload.IPayload
import org.json.JSONArray

open class WitnessConfig(schema: String = ModuleConfig.schema): ModuleConfig(schema) {
    companion object {
        const val schema = "network.xyo.witness.config"
    }
}
open class WitnessParams<TConfig: WitnessConfig>(account: Account, config: TConfig): ModuleParams<TConfig>(account, config)

interface IWitness<TConfig: WitnessConfig, TParams : WitnessParams<TConfig>, TResolver : ModuleResolver> : IModule<TConfig, TParams, TResolver> {
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