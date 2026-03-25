package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.model.BlockRate
import network.xyo.chain.protocol.model.TimeConfig
import network.xyo.chain.protocol.model.TimeUnit
import network.xyo.chain.protocol.model.XL1BlockRange
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

    override suspend fun currentBlockHash(): String {
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_HASH, schemas = schemas)
    }

    override suspend fun currentBlockNumber(): XL1BlockNumber {
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_NUMBER, schemas = schemas)
    }

    override suspend fun chainId(blockNumber: XL1BlockNumber?): ChainId {
        val params = if (blockNumber != null) listOf(blockNumber.value) else emptyList()
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CHAIN_ID, params, schemas)
    }

    override suspend fun payloadByHash(hash: String): Payload? {
        val results = payloadsByHash(listOf(hash))
        return results.firstOrNull()
    }

    override suspend fun payloadsByHash(hashes: List<String>): List<Payload> {
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH, listOf(hashes), schemas)
    }

    override suspend fun rate(range: XL1BlockRange, timeUnit: TimeUnit): BlockRate {
        return transport.sendRequest(
            RpcMethodNames.BLOCK_VIEWER_RATE,
            listOf(listOf(range.start.value, range.end.value), timeUnit.name),
            schemas,
        )
    }

    override suspend fun stepSizeRate(start: XL1BlockNumber, stepIndex: Int, count: Int?, timeUnit: TimeUnit): BlockRate {
        val params = listOfNotNull(start.value, stepIndex, count, timeUnit.name)
        return transport.sendRequest("blockViewer_stepSizeRate", params, schemas)
    }

    override suspend fun timeDurationRate(timeConfig: TimeConfig, startBlockNumber: XL1BlockNumber?, timeUnit: TimeUnit, toleranceMs: Long?, maxAttempts: Int?): BlockRate {
        val params = buildList<Any> {
            add(timeConfig)
            startBlockNumber?.let { add(it.value) }
            add(timeUnit.name)
            toleranceMs?.let { add(it) }
            maxAttempts?.let { add(it) }
        }
        return transport.sendRequest("blockViewer_timeDurationRate", params, schemas)
    }
}
