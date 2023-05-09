package network.xyo.client.module

import android.content.res.Resources.NotFoundException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import network.xyo.client.CompositeModuleResolver
import network.xyo.client.address.Account
import network.xyo.client.boundwitness.XyoBoundWitnessBodyInterface
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.payload.Payload
import java.security.InvalidParameterException

interface ModuleConfig {
    var name: String
    var schema: String
}

interface ModuleParams<TConfig : ModuleConfig> {
    var config: TConfig
    var account: Account
}

interface ModuleFilter {
    var address: List<String>
    var name: List<String>
    var query: List<List<String>>
}

interface ModuleResolver {
    fun addResolver(resolver: ModuleResolver): ModuleResolver
    fun removeResolver(resolver: ModuleResolver): ModuleResolver
    fun resolve(filter: ModuleFilter?): List<Module<ModuleConfig, ModuleParams<ModuleConfig>>>
}

interface QueryBoundWitness: XyoBoundWitnessBodyInterface {
    var query: String
    var resultSet: String
}

typealias ModuleQueryResult = Pair<XyoBoundWitnessJson, List<Payload>>

interface Module<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>> {
    val address: String
    val config: TConfig
    var downResolver: ModuleResolver
    val params: TParams
    var queries: List<String>
    fun query(query: QueryBoundWitness, payloads: List<Payload>?): ModuleQueryResult
    fun queryable(query: QueryBoundWitness, payloads: List<Payload>?): Boolean
    fun start()
    var upResolver: ModuleResolver
}

abstract class AnyModule : Module<ModuleConfig, ModuleParams<ModuleConfig>> {

}

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

    private fun parseQuery(query: QueryBoundWitness, payloads: List<Payload>?): Query {
        val queryPayload = payloads?.first { payload -> payload.hash() == query.query }
        if (queryPayload == null) {
            throw NotFoundException()
        } else {
            return queryPayload as Query
        }
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

    fun bindResult(results: List<Payload>, queryAccount: Account?): ModuleQueryResult {
        return Pair(XyoBoundWitnessBuilder().build(), results)
    }

    protected fun queryHandler(
        query: QueryBoundWitness,
        payloads: List<Payload>?
    ): ModuleQueryResult {
        this.started(notStartedActionEnum.THROW)
        val typedQuery = this.parseQuery(query, payloads)

        if (!this.queryable(query, payloads)) {
            throw InvalidParameterException()
        }
        val resultPayloads = mutableListOf<Payload>()
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

    override fun query(query: QueryBoundWitness, payloads: List<Payload>?): ModuleQueryResult {
        this.started(notStartedActionEnum.THROW)
        return this.queryHandler(query, payloads)
    }

    override fun queryable(query: QueryBoundWitness, payloads: List<Payload>?): Boolean {
        if (!this.started(notStartedActionEnum.WARN))
                return false

        return true
    }

    override fun start() {

    }
    override var upResolver: ModuleResolver = CompositeModuleResolver()
}