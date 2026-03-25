package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.TransactionViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.transaction.SignedHydratedTransactionWithHashMeta
import network.xyo.chain.protocol.viewer.TransactionViewer

class JsonRpcTransactionViewer(
    private val transport: RpcTransport,
) : TransactionViewer {
    override val moniker: String = TransactionViewer.MONIKER

    private val schemas = TransactionViewerRpcSchemas

    override suspend fun byHash(transactionHash: String): SignedHydratedTransactionWithHashMeta? {
        return transport.sendRequest(RpcMethodNames.TX_VIEWER_BY_HASH, listOf(transactionHash), schemas)
    }
}
