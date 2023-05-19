package network.xyo.diviner

import android.content.Context
import network.xyo.account.Account
import network.xyo.module.AbstractModule
import network.xyo.module.ModuleConfig
import network.xyo.module.ModuleParams
import network.xyo.payload.IPayload

open class DivinerConfig(schema: String = ModuleConfig.schema): ModuleConfig(schema) {
    companion object {
        const val schema = "network.xyo.diviner.config"
    }
}
open class DivinerParams<TConfig: DivinerConfig>(account: Account, config: TConfig): ModuleParams<TConfig>(account, config)

open class Diviner<TConfig: DivinerConfig, TParams : DivinerParams<TConfig>>(params: TParams) : AbstractModule<TConfig, TParams>(params) {
    open fun divine(context: Context, payloads: List<IPayload>?): List<IPayload> {
        return payloads ?: emptyList()
    }
}