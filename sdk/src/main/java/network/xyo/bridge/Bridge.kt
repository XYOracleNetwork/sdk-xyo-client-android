package network.xyo.bridge

import network.xyo.account.Account
import network.xyo.module.AbstractModule
import network.xyo.module.IModule
import network.xyo.module.ModuleConfig
import network.xyo.module.ModuleParams

open class BridgeConfig(schema: String = ModuleConfig.schema): ModuleConfig(schema) {
    companion object {
        const val schema = "network.xyo.bridge.config"
    }
}
open class BridgeParams<TConfig: BridgeConfig>(account: Account, config: TConfig): ModuleParams<TConfig>(account, config)

typealias IBridge = IModule<BridgeConfig, BridgeParams<BridgeConfig>, *>

open class Bridge<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
}