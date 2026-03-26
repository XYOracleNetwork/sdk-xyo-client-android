package network.xyo.client.module.model

import network.xyo.client.account.model.AccountInstance

/**
 * Parameters for constructing a module instance, matching JS ModuleParams.
 */
interface ModuleParams<TConfig : ModuleConfig> {
    /** The account used for signing by this module. */
    val account: AccountInstance

    /** The module configuration. */
    val config: TConfig
}
