package network.xyo.module

import android.content.res.Resources.NotFoundException

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import network.xyo.resolver.CompositeModuleResolver
import network.xyo.account.Account
import network.xyo.boundwitness.BoundWitnessBuilder
import network.xyo.boundwitness.IBoundWitness
import network.xyo.boundwitness.QueryBoundWitness
import network.xyo.payload.IPayload
import network.xyo.payload.JSONPayload
import network.xyo.xyoScope

import org.json.JSONException
import java.security.InvalidParameterException

interface IModuleConfig {
    var name: String?
}

open class ModuleConfig(schema: String = Companion.schema): JSONPayload(schema) {
    var name: String?
        get() {
            try {
                return this.getString("name")
            } catch (e: JSONException) {
                return null
            }
        }
        set(value) {
            this.put("name", value)
        }

    companion object {
        const val schema = "network.xyo.module.config"
    }
}

open class ModuleParams<TConfig : ModuleConfig>(val account: Account, val config: TConfig, val scope: CoroutineScope = xyoScope)

class ModuleFilter(
    var address: Set<String>? = null,
    var name: Set<String>? = null,
    var query: Set<List<String>>? = null
)

interface ModuleResolver {
    fun addResolver(resolver: ModuleResolver): ModuleResolver
    fun removeResolver(resolver: ModuleResolver): ModuleResolver
    suspend fun resolve(filter: ModuleFilter? = null): Set<AnyModule>
}

typealias ModuleQueryResult = Pair<IBoundWitness, Set<IPayload>>

interface IModule<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>, TResolver : ModuleResolver> {
    val address: String
    val config: TConfig
    var downResolver: TResolver
    val params: TParams
    var queries: Set<String>
    suspend fun query(query: QueryBoundWitness, payloads: Set<IPayload>?): ModuleQueryResult
    fun queryable(query: QueryBoundWitness, payloads: Set<IPayload>?): Boolean
    suspend fun start()
    var upResolver: TResolver
}

typealias AnyModule = IModule<*, *, *>

open class AbstractModule<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(
    final override var params: TParams
    ) : IModule<TConfig, TParams, CompositeModuleResolver> {

    final override var downResolver = CompositeModuleResolver()
    final override var upResolver = CompositeModuleResolver()
    override var queries: Set<String> = setOf(ModuleDiscoverQuerySchema, ModuleSubscribeQuerySchema)

    private var _started = false

    var account: Account
        get() {
            return this.params.account
        }
        set(value) {}

    override val address: String
        get() {
            return this.params.account.address.hex
        }

    override var config: TConfig
        get() {
            return this.params.config
        }
        set(value) {}

    open fun started(notStartedAction: NotStartedActionEnum = NotStartedActionEnum.THROW): Boolean {
        if (!this._started) {
            when(notStartedAction) {
                NotStartedActionEnum.THROW -> {
                    throw InternalError()
                }
                NotStartedActionEnum.WARN -> {
                    Log.w("AbstractModule", "Not started")
                }
                NotStartedActionEnum.ERROR -> {
                    Log.e("AbstractModule", "Not started")
                }
                else -> {
                    Log.d("AbstractModule", "Not started")
                }
            }
        }
        return this._started
    }

    protected fun parseQuery(query: QueryBoundWitness, payloads: Set<IPayload>?): IPayload {
        val queryPayload = payloads?.first { payload ->
            val hash = payload.hash()
            val queryHash = query.query
            hash == queryHash
        }
        if (queryPayload == null) {
            throw NotFoundException()
        } else {
            return queryPayload
        }
    }

    suspend fun resolve(filter: ModuleFilter?): Set<AnyModule> {
        this.params.scope.run {
            val downModules = this@AbstractModule.downResolver.resolve(filter)
            val upModules = this@AbstractModule.upResolver.resolve(filter)
            val resultAddressSet = mutableSetOf<String>()
            val resultModuleSet = mutableSetOf<AnyModule>()
            for (downModule in downModules) {
                if (!resultAddressSet.contains(downModule.address)) {
                    resultAddressSet.add(downModule.address)
                    resultModuleSet.add(downModule)
                }
            }
            for (upModule in upModules) {
                if (!resultAddressSet.contains(upModule.address)) {
                    resultAddressSet.add(upModule.address)
                    resultModuleSet.add(upModule)
                }
            }
            return resultModuleSet
        }
    }

    open suspend fun discover(): Set<IPayload> {
        return setOf()
    }

    open fun previousHash(): List<IPayload> {
        val fields = mapOf<String, Any?>(Pair("huri", this.account.previousHash))
        val previous = JSONPayload("network.xyo.huri", fields)
        return listOf(previous)
    }

    open fun subscribe() {

    }

    open fun bindResult(payloads: Set<IPayload>, queryAccount: Account?): ModuleQueryResult {
        return Pair(BoundWitnessBuilder().payloads(payloads).signer(queryAccount).build(), payloads)
    }

    protected open suspend fun queryHandler(
        query: QueryBoundWitness,
        payloads: Set<IPayload>?
    ): ModuleQueryResult {
        this.started(NotStartedActionEnum.THROW)
        val typedQuery = this.parseQuery(query, payloads)

        if (!this.queryable(query, payloads)) {
            throw InvalidParameterException()
        }
        val resultPayloads = mutableSetOf<IPayload>()
        val queryAccount = Account()
        try {
            when (typedQuery.schema) {
                ModuleDiscoverQuerySchema -> {
                    resultPayloads.addAll(this.discover())
                }
                ModulePreviousHashQuerySchema -> {
                    resultPayloads.addAll(this.previousHash())
                }
                ModuleSubscribeQuerySchema -> {
                    this.subscribe()
                }
                else -> {
                    Log.e("Unsupported Query", query.schema)
                }
            }
        } catch (error: Error) {
            val errorPayload = JSONPayload("network.xyo.error")
            resultPayloads.add(errorPayload)
        }
        return this.bindResult(resultPayloads, queryAccount)
    }

    override suspend fun query(query: QueryBoundWitness, payloads: Set<IPayload>?): ModuleQueryResult {
        this.started(NotStartedActionEnum.THROW)
        return this.queryHandler(query, payloads)
    }

    override fun queryable(query: QueryBoundWitness, payloads: Set<IPayload>?): Boolean {
        if (!this.started(NotStartedActionEnum.WARN))
                return false
        val typedQuery = this.parseQuery(query, payloads)
        return this.queries.contains(typedQuery.schema)
    }

    override suspend fun start() {
        this.downResolver.add(this)
        this._started = true
    }

    companion object {
        const val ModuleDiscoverQuerySchema = "network.xyo.query.module.discover"
        const val ModulePreviousHashQuerySchema = "network.xyo.query.module.account.hash.previous"
        const val ModuleSubscribeQuerySchema = "network.xyo.query.module.subscribe"

        enum class NotStartedActionEnum {
            ERROR, THROW, WARN, LOG, NONE
        }
    }

    init {
        val downResolver = CompositeModuleResolver()
        this.downResolver = downResolver
    }
}