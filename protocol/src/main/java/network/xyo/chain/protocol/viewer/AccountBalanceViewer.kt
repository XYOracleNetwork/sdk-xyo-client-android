package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.xl1.AttoXL1

interface AccountBalanceViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun qualifiedAccountBalances(addresses: List<String>, config: AccountBalanceConfig): ChainQualified<Map<String, AttoXL1>>
    suspend fun qualifiedAccountBalanceHistories(addresses: List<String>, config: AccountBalanceConfig): ChainQualified<Map<String, List<AccountBalanceHistoryItem>>>

    companion object {
        const val MONIKER = "AccountBalanceViewer"
    }
}
