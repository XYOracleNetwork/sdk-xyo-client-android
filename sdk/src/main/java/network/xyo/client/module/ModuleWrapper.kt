package network.xyo.client.module

import network.xyo.client.address.Account
import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.boundwitness.QueryBoundWitness
import network.xyo.client.boundwitness.QueryBoundWitnessBuilder
import network.xyo.client.payload.Payload

open class ModuleWrapper<TConfig: ModuleConfig, TParams: ModuleParams<TConfig>, TModule: Module<TConfig, TParams>>(val module: TModule, val account: Account) {

    val address: String
        get() {
            return this.module.address
        }

    val config: TConfig
        get() {
            return this.module.config
        }

    val queries: List<String>
        get() {
            return this.module.queries
        }

    suspend fun query(query: QueryBoundWitness, payloads: Set<Payload>? = null): ModuleQueryResult {
        return this.module.query(query, payloads)
    }

    protected fun bindQuery(query: Payload, payloads: Set<Payload> = emptySet(), account: Account = this.account): Pair<QueryBoundWitness, Set<Payload>> {
        return Pair(QueryBoundWitnessBuilder().payloads(payloads).witness(account).query(query).build(), payloads)
    }

    protected suspend fun sendQuery(queryPayload: Payload, payloads: Set<Payload> = emptySet()): ModuleQueryResult {
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