package network.xyo.archivist

import android.util.LruCache
import network.xyo.boundwitness.QueryBoundWitness
import network.xyo.resolver.CompositeModuleResolver
import network.xyo.account.Account
import network.xyo.module.AbstractModule
import network.xyo.module.IModule
import network.xyo.module.ModuleConfig
import network.xyo.module.ModuleParams
import network.xyo.module.ModuleQueryResult
import network.xyo.module.ModuleResolver
import network.xyo.payload.IPayload
import network.xyo.payload.JSONPayload
import java.security.InvalidParameterException

open class ArchivistConfig(schema: String = ModuleConfig.schema): ModuleConfig(schema) {
    companion object {
        const val schema = "network.xyo.archivist.config"
    }
}
open class ArchivistParams<TConfig: ArchivistConfig>(account: Account, config: TConfig): ModuleParams<TConfig>(account, config)

interface IArchivist<TConfig: ArchivistConfig, TParams : ArchivistParams<TConfig>, TResolver : ModuleResolver> :
    IModule<TConfig, TParams, TResolver>

typealias AnyArchivist = IArchivist<*, *, *>

open class Archivist<TConfig: ArchivistConfig, TParams : ArchivistParams<TConfig>>(
    params: TParams
) : IArchivist<TConfig, TParams, CompositeModuleResolver>, AbstractModule<TConfig, TParams>(params) {

    private val cache = LruCache<String, IPayload>(1000)
    override var queries: Set<String> = setOf( *super.queries.toTypedArray(), ArchivistGetQueryPayload.schema, ArchivistInsertQueryPayload.schema )


    fun get(ids: Set<String>?): Set<IPayload> {
        return ids?.map { id -> this.cache.get(id) }?.toSet() ?: this.all()
    }
    fun insert(payloads: Set<IPayload>): Set<IPayload> {
        payloads.forEach { payload -> this.cache.put(payload.hash(), payload) }
        return payloads
    }

    open fun all(): Set<IPayload> {
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

    override suspend fun queryHandler(
        query: QueryBoundWitness,
        payloads: Set<IPayload>?
    ): ModuleQueryResult {
        this.started(Companion.NotStartedActionEnum.THROW)
        val typedQuery = this.parseQuery(query, payloads)

        if (!this.queryable(query, payloads)) {
            throw InvalidParameterException()
        }
        val resultPayloads = mutableSetOf<IPayload>()
        val queryAccount = Account()
        try {
            when (typedQuery.schema) {
                ArchivistInsertQueryPayload.schema -> {
                    this.insert(payloads ?: emptySet())
                }
                ArchivistGetQueryPayload.schema -> {
                    this.get((payloads?.find { payload -> payload.schema == ArchivistGetQueryPayload.schema } as ArchivistGetQueryPayload?)?.hashes ?: emptySet() )
                }
                else -> {
                    return super.queryHandler(query, payloads)
                }
            }
        } catch (error: Error) {
            val errorPayload = JSONPayload("network.xyo.error")
            resultPayloads.add(errorPayload)
        }
        return this.bindResult(resultPayloads, queryAccount)
    }
}