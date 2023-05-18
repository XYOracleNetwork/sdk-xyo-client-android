package network.xyo.sentinel

import network.xyo.client.CompositeModuleResolver
import network.xyo.client.address.Account
import network.xyo.client.archivist.wrapper.ArchivistWrapper
import network.xyo.client.module.AbstractModule
import network.xyo.client.module.AnyArchivist
import network.xyo.client.module.AnyWitness
import network.xyo.client.module.ArchivistConfig
import network.xyo.client.module.ArchivistParams
import network.xyo.client.module.IArchivist
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleFilter
import network.xyo.client.module.ModuleParams
import network.xyo.payload.IPayload
import org.json.JSONArray

open class SentinelConfig(schema: String = Companion.schema): ModuleConfig(schema) {

    constructor(witnesses: Set<String>, archivists: Set<String>): this() {
        this.witnesses = witnesses
        this.archivists = archivists
    }

    var witnesses: Set<String>
        get() {
            return this.getArrayAsStringSet("witnesses")
        }
        set(value) {
            this.put("witnesses", JSONArray(value))
        }

    var archivists: Set<String>
        get() {
            return this.getArrayAsStringSet("archivists")
        }
        set(value) {
            this.put("archivists", JSONArray(value))
        }

    companion object {
        const val schema = "network.xyo.sentinel.config"
    }
}

open class SentinelParams<TConfig: SentinelConfig>(account: Account, config: TConfig): ModuleParams<TConfig>(account, config)

open class Sentinel<TConfig: SentinelConfig>(params: SentinelParams<TConfig>): AbstractModule<TConfig, ModuleParams<TConfig>>(params) {

    private var _archivists: Set<AnyArchivist>? = null

    suspend fun getArchivists(account: Account? = null): Set<AnyArchivist> {
        val addresses = this.config.archivists
        val filter = ModuleFilter(addresses)
        this._archivists = this._archivists ?: this.resolve(filter).map { module -> module as AnyArchivist}.toSet()
        if (addresses.size != this._archivists?.size) {
            print("Not all archivists found [Requested: ${addresses.size}, Found: ${this._archivists?.size}]")
        }

        return this._archivists!!
    }

    private var _witnesses: Set<AnyWitness>? = null

    suspend fun getWitnesses(account: Account? = null): Set<AnyWitness> {
        val witnesses = this.config.witnesses
        val filter = ModuleFilter(witnesses)
        this._witnesses = this._witnesses ?: this.resolve(filter).map { module -> module as AnyWitness }.toSet()
        if (witnesses.size != this._witnesses?.size) {
            print("Not all witnesses found [Requested: ${witnesses.size}, Found: ${this._witnesses?.size}]")
        }

        return this._witnesses!!
    }

    suspend fun report(payloads: Set<IPayload> = emptySet()): Set<IPayload> {
        val result = setOf(*(this.getWitnesses().map {
                witness ->
            witness.observe()
        }.flatten().toTypedArray()), *payloads.toTypedArray())

        this.getArchivists(this.account).forEach { archivist ->
            val wrapper = ArchivistWrapper<ArchivistConfig, ArchivistParams<ArchivistConfig>, AnyArchivist>(archivist)
            wrapper.insert(result)
        }

        return result
    }
}