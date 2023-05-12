package network.xyo.client.module

import android.util.LruCache
import network.xyo.payload.Payload

typealias AnyArchivist = Module<*, *>

open class Archivist<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {

    private val cache = LruCache<String, Payload>(1000)

    fun get(ids: Set<String>?): Set<Payload> {
        return ids?.map { id -> this.cache.get(id) }?.toSet() ?: this.all()
    }
    fun insert(payloads: Set<Payload>): Set<Payload> {
        payloads.forEach { payload -> this.cache.put(payload.hash(), payload) }
        return payloads
    }

    open fun all(): Set<Payload> {
        return this.cache.snapshot().values.toSet()
    }

    open fun clear() {
        this.cache.evictAll()
    }

    open fun commit() {
        throw NotImplementedError()
    }

    open fun delete(ids: Set<String>) {
        ids.forEach { id -> this.cache.remove(id)}
    }
}