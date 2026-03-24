package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.xl1.AttoXL1

typealias TransferPair = Pair<String, String>

data class TransferBalanceHistoryItem(
    val block: Any,
    val transaction: Any?,
    val transfer: Any,
)

interface TransferBalanceViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun transferBalance(address: String): AttoXL1
    suspend fun transferBalanceHistory(address: String, range: XL1BlockRange? = null): List<TransferBalanceHistoryItem>
    suspend fun transferBalances(addresses: List<String>): Map<String, Map<String, AttoXL1>>
    suspend fun transferPairBalance(pair: TransferPair): AttoXL1
    suspend fun transferPairBalanceHistory(pair: TransferPair): List<TransferBalanceHistoryItem>
    suspend fun transferPairBalances(pairs: List<TransferPair>): Map<String, Map<String, AttoXL1>>

    companion object {
        const val MONIKER = "TransferBalanceViewer"
    }
}
