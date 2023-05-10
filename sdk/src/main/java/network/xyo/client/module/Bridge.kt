package network.xyo.client.module

typealias AnyBridge = Module<ModuleConfig, ModuleParams<ModuleConfig>>

open class Bridge<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
}