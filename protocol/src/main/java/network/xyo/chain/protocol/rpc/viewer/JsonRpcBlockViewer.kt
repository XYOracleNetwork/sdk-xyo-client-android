package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.schema.BlockViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.BlockViewer
import network.xyo.client.payload.model.Payload

class JsonRpcBlockViewer(
    private val transport: RpcTransport,
) : BlockViewer {
    override val moniker: String = BlockViewer.MONIKER

    private val schemas = BlockViewerRpcSchemas

    override suspend fun blockByHash(hash: String): SignedHydratedBlockWithHashMeta? {
        val results = blocksByHash(hash, 1)
        return results.firstOrNull()
    }

    override suspend fun blockByNumber(block: XL1BlockNumber): SignedHydratedBlockWithHashMeta? {
        val results = blocksByNumber(block, 1)
        return results.firstOrNull()
    }

    override suspend fun blocksByHash(hash: String, limit: Int?): List<SignedHydratedBlockWithHashMeta> {
        val params = if (limit != null) listOf(hash, limit) else listOf(hash)
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_HASH, params, schemas)
    }

    override suspend fun blocksByNumber(block: XL1BlockNumber, limit: Int?): List<SignedHydratedBlockWithHashMeta> {
        val params = if (limit != null) listOf(block.value, limit) else listOf(block.value)
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_NUMBER, params, schemas)
    }

    override suspend fun currentBlock(): SignedHydratedBlockWithHashMeta {
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK, schemas = schemas)
    }

    override suspend fun payloadsByHash(hashes: List<String>): List<Payload> {
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH, listOf(hashes), schemas)
    }
}
