package network.xyo.client.module

import android.content.Context
import android.util.Log
import network.xyo.client.CompositeModuleResolver
import network.xyo.client.address.Account
import network.xyo.client.payload.Payload

open class SentinelConfig(schema: String = SentinelConfig.schema): ModuleConfig(schema) {

    constructor(witnesses: Set<String>, archivists: Set<String>): this() {
        this.witnesses = witnesses
        this.archivists = archivists
    }

    var witnesses: Set<String>
        get() {
            return this.getArrayAsStringSet("witnesses")
        }
        set(value) {
            this.put("witnesses", value)
        }

    var archivists: Set<String>
        get() {
            return this.getArrayAsStringSet("archivists")
        }
        set(value) {
            this.put("archivists", value)
        }

    companion object {
        const val schema = "network.xyo.sentinel.config"
    }
}

class Sentinel<TConfig: SentinelConfig>(val context: Context, params: ModuleParams<SentinelConfig>): AbstractModule<SentinelConfig, ModuleParams<SentinelConfig>>(params) {
    override var downResolver: ModuleResolver = CompositeModuleResolver()

    private var _archivists: Set<AnyArchivist>? = null

    suspend fun getArchivists(account: Account? = null): Set<AnyArchivist> {
        val addresses = this.config.archivists
        val filter = ModuleFilter(addresses)
        this._archivists = this._archivists ?: this.resolve(filter)
        if (addresses.size != this._archivists?.size) {
            Log.w("Sentinel", "Not all archivists found [Requested: ${addresses.size}, Found: ${this._archivists?.size}]")
        }

        return this._archivists!!
    }

    private var _witnesses: Set<AnyWitness>? = null

    suspend fun getWitnesses(account: Account? = null): Set<AnyWitness> {
        val witnesses = this.config.witnesses
        val filter = ModuleFilter(witnesses)
        this._witnesses = this._witnesses ?: this.resolve(filter)
        if (witnesses.size != this._witnesses?.size) {
            Log.w("Sentinel", "Not all witnesses found [Requested: ${witnesses.size}, Found: ${this._witnesses?.size}]")
        }

        return this._witnesses!!
    }

    fun report(payloads: List<Payload> = emptyList()) {

    }
}