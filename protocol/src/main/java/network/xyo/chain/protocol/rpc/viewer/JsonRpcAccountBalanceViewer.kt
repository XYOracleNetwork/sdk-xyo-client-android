package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.rpc.schema.AccountBalanceViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.AccountBalanceViewer
import network.xyo.chain.protocol.xl1.AttoXL1

class JsonRpcAccountBalanceViewer(
    private val transport: RpcTransport,
) : AccountBalanceViewer {
    override val moniker: String = AccountBalanceViewer.MONIKER

    private val schemas = AccountBalanceViewerRpcSchemas

    override suspend fun qualifiedAccountBalances(addresses: List<String>, config: AccountBalanceConfig): ChainQualified<Map<String, AttoXL1>> {
        return transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES, listOf(addresses, config), schemas)
    }

    override suspend fun qualifiedAccountBalanceHistories(addresses: List<String>, config: AccountBalanceConfig): ChainQualified<Map<String, List<AccountBalanceHistoryItem>>> {
        return transport.sendRequest(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES, listOf(addresses, config), schemas)
    }
}
