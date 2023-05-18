package network.xyo.client.module

import android.content.Context
import network.xyo.client.address.Account
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