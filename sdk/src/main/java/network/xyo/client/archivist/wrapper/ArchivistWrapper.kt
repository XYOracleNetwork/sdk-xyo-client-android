package network.xyo.client.archivist.wrapper

import network.xyo.client.address.Account
import network.xyo.client.module.Module
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.ModuleQueryResult
import network.xyo.client.module.ModuleWrapper
import network.xyo.payload.Payload

open class ArchivistWrapper<TConfig: ModuleConfig, TParams: ModuleParams<TConfig>, TModule: Module<TConfig, TParams>>(
        module: TModule,
        account: Account
    ): ModuleWrapper<TConfig, TParams, TModule>(module, account) {
    suspend fun get(hashes: List<String>): ModuleQueryResult {
        return this.sendQuery(ArchivistGetQueryPayload(hashes))
    }

    suspend fun insert(payloads: Set<Payload>): ModuleQueryResult {
        return this.sendQuery(ArchivistInsertQueryPayload())
    }
}