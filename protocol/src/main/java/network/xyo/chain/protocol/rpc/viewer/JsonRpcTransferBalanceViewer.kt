package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.rpc.schema.TransferBalanceViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.TransferBalanceHistoryItem
import network.xyo.chain.protocol.viewer.TransferBalanceViewer
import network.xyo.chain.protocol.viewer.TransferPair
import network.xyo.chain.protocol.xl1.AttoXL1

class JsonRpcTransferBalanceViewer(
    private val transport: RpcTransport,
) : TransferBalanceViewer {
    override val moniker: String = TransferBalanceViewer.MONIKER

    private val schemas = TransferBalanceViewerRpcSchemas

    override suspend fun transferBalance(address: String): AttoXL1 {
        return transport.sendRequest(RpcMethodNames.TRANSFER_BALANCE_VIEWER_BALANCE, listOf(address), schemas)
    }

    override suspend fun transferBalanceHistory(address: String, range: XL1BlockRange?): List<TransferBalanceHistoryItem> {
        return transport.sendRequest(RpcMethodNames.TRANSFER_BALANCE_VIEWER_HISTORY, listOf(address, range), schemas)
    }

    override suspend fun transferBalances(addresses: List<String>): Map<String, Map<String, AttoXL1>> {
        return transport.sendRequest(RpcMethodNames.TRANSFER_BALANCE_VIEWER_BALANCES, listOf(addresses), schemas)
    }

    override suspend fun transferPairBalance(pair: TransferPair): AttoXL1 {
        return transport.sendRequest(RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_BALANCE, listOf(pair.first, pair.second), schemas)
    }

    override suspend fun transferPairBalanceHistory(pair: TransferPair): List<TransferBalanceHistoryItem> {
        return transport.sendRequest(RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_HISTORY, listOf(pair.first, pair.second), schemas)
    }

    override suspend fun transferPairBalances(pairs: List<TransferPair>): Map<String, Map<String, AttoXL1>> {
        return transport.sendRequest(RpcMethodNames.TRANSFER_BALANCE_VIEWER_PAIR_BALANCES, listOf(pairs), schemas)
    }
}
