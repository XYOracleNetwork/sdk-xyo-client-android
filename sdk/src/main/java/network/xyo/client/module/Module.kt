package network.xyo.client.module

import android.content.res.Resources.NotFoundException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import network.xyo.client.CompositeModuleResolver
import network.xyo.client.XyoSerializable
import network.xyo.client.address.XyoAccount
import network.xyo.client.boundwitness.XyoBoundWitnessBodyInterface
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.payload.XyoPayload
import java.security.InvalidParameterException

interface ModuleConfig {
    var name: String
    var schema: String
}

interface ModuleParams<TConfig : ModuleConfig> {
    var config: TConfig
    var account: XyoAccount
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

interface Module<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>> {
    var address: String
    var config: TConfig
    var downResolver: ModuleResolver
    var params: TParams
    var queries: List<String>
    fun query(query: QueryBoundWitness, payloads: List<XyoPayload>?): Pair<XyoBoundWitnessBodyInterface, List<XyoPayload>>
    fun queryable(query: QueryBoundWitness, payloads: List<XyoPayload>?): Boolean
    fun start()
    var upResolver: ModuleResolver
}

interface AnyModule : Module<ModuleConfig, ModuleParams<ModuleConfig>> {

}

@RequiresApi(Build.VERSION_CODES.M)
open class AbstractModule<TConfig: ModuleConfig, TParams : ModuleParams<TConfig>>(override var params: TParams) : Module<TConfig, TParams> {

    val ModuleDiscoverQuerySchema = "network.xyo.query.module.discover"
    val ModulePreviousHashQuerySchema = "network.xyo.query.module.account.hash.previous"
    val ModuleSubscribeQuerySchema = "network.xyo.query.module.subscribe"

    enum class notStartedActionEnum {
        ERROR, THROW, WARN, LOG, NONE
    }

    private var _started = false

    var account: XyoAccount
        get() {
            return this.params.account
        }
        set(value) {}

    override var address: String
        get() {
            return this.params.account.address.hex
        }
        set(value) {}

    override var config: TConfig
        get() {
            return this.params.config
        }
        set(value) {}

    fun started(notStartedAction: notStartedActionEnum): Boolean {
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
    override var queries = listOf<String>()

    private fun parseQuery(query: QueryBoundWitness, payloads: List<XyoPayload>?): XyoPayload {
        val queryPayload = payloads?.first { payload -> XyoSerializable.sha256String(payload) == query.query }
        if (queryPayload == null) {
            throw NotFoundException()
        } else {
            return queryPayload
        }
    }

    fun discover(): List<XyoPayload> {
        return listOf<XyoPayload>()
    }

    fun subscribe() {

    }

    fun bindResult(results: List<XyoPayload>, queryAccount: XyoAccount?): Pair<XyoBoundWitnessJson, List<XyoPayload>> {
        return Pair(XyoBoundWitnessBuilder().build(), results)
    }

    fun previousHash(): List<XyoPayload> {
        val previous = XyoPayload("network.xyo.huri", this.account.previousHash)
        return listOf(previous)
    }

    protected fun queryHandler(
        query: QueryBoundWitness,
        payloads: List<XyoPayload>?
    ): Pair<XyoBoundWitnessJson, List<XyoPayload>> {
        this.started(notStartedActionEnum.THROW)
        val typedQuery = this.parseQuery(query, payloads)

        if (!this.queryable(query, payloads)) {
            throw InvalidParameterException()
        }
        val resultPayloads = mutableListOf<XyoPayload>()
        val queryAccount = XyoAccount()
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
            val errorPayload = XyoPayload("network.xyo.error")
            resultPayloads.add(errorPayload)
        }
        return this.bindResult(resultPayloads, queryAccount)
    }

    override fun query(query: QueryBoundWitness, payloads: List<XyoPayload>?): Pair<XyoBoundWitnessJson, List<XyoPayload>> {
        this.started(notStartedActionEnum.THROW)
        return this.queryHandler(query, payloads)
    }

    override fun queryable(query: QueryBoundWitness, payloads: List<XyoPayload>?): Boolean {
        if (!this.started(notStartedActionEnum.WARN))
                return false

        return true
    }

    override fun start() {

    }
    override var upResolver: ModuleResolver = CompositeModuleResolver()
}