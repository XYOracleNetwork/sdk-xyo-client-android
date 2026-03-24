package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.AccountBalanceViewer
import network.xyo.chain.protocol.xl1.AttoXL1
import java.math.BigInteger

class JsonRpcAccountBalanceViewer(
    private val transport: RpcTransport,
) : AccountBalanceViewer {
    override val moniker: String = AccountBalanceViewer.MONIKER

    override suspend fun accountBalance(address: String, config: AccountBalanceConfig?): AttoXL1 {
        val result = accountBalances(listOf(address), config)
        return result[address] ?: AttoXL1.ZERO
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun accountBalances(addresses: List<String>, config: AccountBalanceConfig?): Map<String, AttoXL1> {
        val params = if (config != null) listOf(addresses, config) else listOf(addresses)
        val result = transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_BALANCES, params)
        val map = result as? Map<String, Any> ?: return emptyMap()
        return map.mapValues { (_, v) ->
            when (v) {
                is String -> AttoXL1.fromHex(v)
                is Number -> AttoXL1(BigInteger.valueOf(v.toLong()))
                else -> AttoXL1.ZERO
            }
        }
    }

    override suspend fun accountBalanceHistory(address: String, config: AccountBalanceConfig?): List<AccountBalanceHistoryItem> {
        val params = if (config != null) listOf(address, config) else listOf(address)
        transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_HISTORY, params)
        // TODO: full deserialization
        return emptyList()
    }

    override suspend fun accountBalanceHistories(addresses: List<String>, config: AccountBalanceConfig?): Map<String, List<AccountBalanceHistoryItem>> {
        val params = if (config != null) listOf(addresses, config) else listOf(addresses)
        transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_HISTORIES, params)
        // TODO: full deserialization
        return emptyMap()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun qualifiedAccountBalances(addresses: List<String>, config: AccountBalanceConfig): ChainQualified<Map<String, AttoXL1>> {
        val result = transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES, listOf(addresses, config))
        // TODO: full deserialization
        return emptyMap()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun qualifiedAccountBalanceHistories(addresses: List<String>, config: AccountBalanceConfig): ChainQualified<Map<String, List<AccountBalanceHistoryItem>>> {
        val result = transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES, listOf(addresses, config))
        // TODO: full deserialization
        return emptyMap()
    }
}
