package network.xyo.client.archivist.wrapper

import network.xyo.client.address.Account
import network.xyo.client.module.AnyModule
import network.xyo.client.module.ArchivistConfig
import network.xyo.client.module.ArchivistParams
import network.xyo.client.module.ModuleQueryResult
import network.xyo.client.module.ModuleWrapper
import network.xyo.payload.IPayload

open class ArchivistWrapper<TConfig: ArchivistConfig, TParams: ArchivistParams<TConfig>, TModule: AnyModule>(
        module: TModule,
        account: Account = Account()
    ): ModuleWrapper<TConfig, TParams, TModule>(module, account) {
    suspend fun get(hashes: Set<String>): ModuleQueryResult {
        return this.sendQuery(ArchivistGetQueryPayload(hashes))
    }

    suspend fun insert(payloads: Set<IPayload>): ModuleQueryResult {
        return this.sendQuery(ArchivistInsertQueryPayload(), payloads)
    }
}