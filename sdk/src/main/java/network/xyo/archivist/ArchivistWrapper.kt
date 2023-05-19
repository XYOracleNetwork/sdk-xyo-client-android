package network.xyo.archivist

import network.xyo.account.Account
import network.xyo.module.AnyModule
import network.xyo.module.ModuleQueryResult
import network.xyo.module.ModuleWrapper
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