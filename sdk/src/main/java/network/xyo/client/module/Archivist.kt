package network.xyo.client.module

import network.xyo.client.payload.Payload

typealias AnyArchivist = Module<ModuleConfig, ModuleParams<ModuleConfig>>

abstract class Archivist<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
    abstract fun get(ids: Set<String>?): Set<Payload>
    abstract fun insert(payloads: Set<Payload>): Set<Payload>

    open fun all(): Set<Payload> {
        throw NotImplementedError()
    }

    open fun clear() {
        throw NotImplementedError()
    }

    open fun commit() {
        throw NotImplementedError()
    }

    open fun delete() {
        throw NotImplementedError()
    }
}