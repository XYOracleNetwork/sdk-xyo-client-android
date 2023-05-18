package network.xyo.client.module

import network.xyo.client.address.Account
import network.xyo.boundwitness.QueryBoundWitness
import network.xyo.boundwitness.QueryBoundWitnessBuilder
import network.xyo.payload.IPayload

open class ModuleWrapper<TConfig: ModuleConfig, TParams: ModuleParams<TConfig>, TModule: AnyModule>(val module: TModule, val account: Account = Account()) {

    val address: String
        get() {
            return this.module.address
        }

    val config: TConfig
        get() {
            return this.module.config as TConfig
        }

    val queries: Set<String>
        get() {
            return this.module.queries
        }

    suspend fun query(query: QueryBoundWitness, payloads: Set<IPayload>? = null): ModuleQueryResult {
        return this.module.query(query, payloads)
    }

    protected fun bindQuery(query: IPayload, payloads: Set<IPayload> = emptySet(), account: Account = this.account): Pair<QueryBoundWitness, Set<IPayload>> {
        return Pair(QueryBoundWitnessBuilder(query).payloads(payloads).witness(account).build(), setOf(*payloads.toTypedArray(), query))
    }

    protected suspend fun sendQuery(queryPayload: IPayload, payloads: Set<IPayload> = emptySet()): ModuleQueryResult {
        // Bind them
        val query = this.bindQuery(queryPayload, payloads)

        // Send them off
        return this.module.query(query.first, query.second)
    }

    suspend fun resolve(filter: ModuleFilter?): Set<AnyModule> {
        val downModules = this.module.downResolver.resolve(filter)
        val upModules = this.module.upResolver.resolve(filter)
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