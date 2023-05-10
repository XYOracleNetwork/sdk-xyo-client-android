package network.xyo.client.module

import android.content.res.Resources.NotFoundException

import android.util.Log

import network.xyo.client.CompositeModuleResolver
import network.xyo.client.address.Account
import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.boundwitness.BoundWitnessBuilder
import network.xyo.client.boundwitness.QueryBoundWitness

import network.xyo.client.payload.Payload
import java.security.InvalidParameterException

open class ModuleConfig(schema: String = ModuleConfig.schema): Payload(schema) {
    var name: String
        get() {
            return this.getString("name")
        }
        set(value) {
            this.put("name", value)
        }

    companion object {
        const val schema = "network.xyo.module.config"
    }
}

open class ModuleParams<TConfig : ModuleConfig>(val account: Account, val config: TConfig)

class ModuleFilter(
    var address: Set<String>? = null,
    var name: Set<String>? = null,
    var query: Set<List<String>>? = null
)

interface ModuleResolver {
    fun addResolver(resolver: ModuleResolver): ModuleResolver
    fun removeResolver(resolver: ModuleResolver): ModuleResolver
    suspend fun resolve(filter: ModuleFilter?): Set<AnyModule>
}

typealias ModuleQueryResult = Pair<BoundWitness, Set<Payload>>

interface Module<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>> {
    val address: String
    val config: TConfig
    var downResolver: ModuleResolver
    val params: TParams
    var queries: List<String>
    suspend fun query(query: QueryBoundWitness, payloads: Set<Payload>?): ModuleQueryResult
    fun queryable(query: QueryBoundWitness, payloads: Set<Payload>?): Boolean
    suspend fun start()
    var upResolver: ModuleResolver
}

typealias AnyModule = Module<ModuleConfig, ModuleParams<ModuleConfig>>

open class AbstractModule<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(override var params: TParams) : Module<TConfig, TParams> {

    val ModuleDiscoverQuerySchema = "network.xyo.query.module.discover"
    val ModulePreviousHashQuerySchema = "network.xyo.query.module.account.hash.previous"
    val ModuleSubscribeQuerySchema = "network.xyo.query.module.subscribe"

    enum class notStartedActionEnum {
        ERROR, THROW, WARN, LOG, NONE
    }

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

    fun started(notStartedAction: notStartedActionEnum = notStartedActionEnum.THROW): Boolean {
        if (!this._started) {
            when(notStartedAction) {
                notStartedActionEnum.THROW -> {
                    throw InternalError()
                }
                notStartedActionEnum.WARN -> {
                    Log.w("AbstractModule", "Not started")
                }
                notStartedActionEnum.ERROR -> {
                    Log.e("AbstractModule", "Not started")
                }
                else -> {
                    Log.d("AbstractModule", "Not started")
                }
            }
        }
        return this._started
    }

    override var downResolver: ModuleResolver = CompositeModuleResolver()
    override var queries = listOf<String>(ModuleDiscoverQuerySchema, ModuleSubscribeQuerySchema)

    private fun parseQuery(query: QueryBoundWitness, payloads: Set<Payload>?): Payload {
        val queryPayload = payloads?.first { payload -> payload.hash() == query.query }
        if (queryPayload == null) {
            throw NotFoundException()
        } else {
            return queryPayload
        }
    }

    suspend fun resolve(filter: ModuleFilter?): Set<AnyModule> {
        val downModules = this.downResolver.resolve(filter)
        val upModules = this.upResolver.resolve(filter)
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

    fun discover(): List<Payload> {
        return listOf<Payload>()
    }

    fun previousHash(): List<Payload> {
        val fields = mapOf<String, Any?>(Pair("huri", this.account.previousHash))
        val previous = Payload("network.xyo.huri", fields)
        return listOf(previous)
    }

    fun subscribe() {

    }

    fun bindResult(payloads: Set<Payload>, queryAccount: Account?): ModuleQueryResult {
        return Pair(BoundWitnessBuilder().payloads(payloads).witness(queryAccount).build(), payloads)
    }

    protected fun queryHandler(
        query: QueryBoundWitness,
        payloads: Set<Payload>?
    ): ModuleQueryResult {
        this.started(notStartedActionEnum.THROW)
        val typedQuery = this.parseQuery(query, payloads)

        if (!this.queryable(query, payloads)) {
            throw InvalidParameterException()
        }
        val resultPayloads = mutableSetOf<Payload>()
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
            val errorPayload = Payload("network.xyo.error")
            resultPayloads.add(errorPayload)
        }
        return this.bindResult(resultPayloads, queryAccount)
    }

    override suspend fun query(query: QueryBoundWitness, payloads: Set<Payload>?): ModuleQueryResult {
        this.started(notStartedActionEnum.THROW)
        return this.queryHandler(query, payloads)
    }

    override fun queryable(query: QueryBoundWitness, payloads: Set<Payload>?): Boolean {
        if (!this.started(notStartedActionEnum.WARN))
                return false

        return true
    }

    override suspend fun start() {

    }
    override var upResolver: ModuleResolver = CompositeModuleResolver()
}