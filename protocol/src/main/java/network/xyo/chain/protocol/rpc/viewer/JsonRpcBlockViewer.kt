package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.SignedBlockBoundWitness
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.model.BlockRate
import network.xyo.chain.protocol.model.TimeConfig
import network.xyo.chain.protocol.model.TimeUnit
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.BlockViewer
import network.xyo.client.payload.model.Payload

class JsonRpcBlockViewer(
    private val transport: RpcTransport,
) : BlockViewer {
    override val moniker: String = BlockViewer.MONIKER

    override suspend fun blockByHash(hash: String): SignedHydratedBlockWithHashMeta? {
        val results = blocksByHash(hash, 1)
        return results.firstOrNull()
    }

    override suspend fun blockByNumber(block: XL1BlockNumber): SignedHydratedBlockWithHashMeta? {
        val results = blocksByNumber(block, 1)
        return results.firstOrNull()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun blocksByHash(hash: String, limit: Int?): List<SignedHydratedBlockWithHashMeta> {
        val params = if (limit != null) listOf(hash, limit) else listOf(hash)
        val result = transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_HASH, params)
        return parseBlockList(result)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun blocksByNumber(block: XL1BlockNumber, limit: Int?): List<SignedHydratedBlockWithHashMeta> {
        val params = if (limit != null) listOf(block.value, limit) else listOf(block.value)
        val result = transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_NUMBER, params)
        return parseBlockList(result)
    }

    override suspend fun currentBlock(): SignedHydratedBlockWithHashMeta {
        val result = transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK)
        return parseBlock(result) ?: error("Current block not found")
    }

    override suspend fun currentBlockHash(): String {
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_HASH) as? String
            ?: error("Current block hash not found")
    }

    override suspend fun currentBlockNumber(): XL1BlockNumber {
        val result = transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_NUMBER)
        return XL1BlockNumber((result as Number).toLong())
    }

    override suspend fun chainId(blockNumber: XL1BlockNumber?): ChainId {
        val params = if (blockNumber != null) listOf(blockNumber.value) else emptyList()
        return transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_CHAIN_ID, params) as? String
            ?: error("Chain ID not found")
    }

    override suspend fun payloadByHash(hash: String): Payload? {
        val results = payloadsByHash(listOf(hash))
        return results.firstOrNull()
    }

    override suspend fun payloadsByHash(hashes: List<String>): List<Payload> {
        transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH, listOf(hashes))
        // TODO: deserialize payload results
        return emptyList()
    }

    override suspend fun rate(range: XL1BlockRange, timeUnit: TimeUnit): BlockRate {
        val result = transport.sendRequest(RpcMethodNames.BLOCK_VIEWER_RATE, listOf(listOf(range.start.value, range.end.value), timeUnit.name))
        return parseBlockRate(result)
    }

    override suspend fun stepSizeRate(start: XL1BlockNumber, stepIndex: Int, count: Int?, timeUnit: TimeUnit): BlockRate {
        val params = listOfNotNull(start.value, stepIndex, count, timeUnit.name)
        val result = transport.sendRequest("blockViewer_stepSizeRate", params)
        return parseBlockRate(result)
    }

    override suspend fun timeDurationRate(timeConfig: TimeConfig, startBlockNumber: XL1BlockNumber?, timeUnit: TimeUnit, toleranceMs: Long?, maxAttempts: Int?): BlockRate {
        val params = buildList<Any> {
            add(timeConfig)
            startBlockNumber?.let { add(it.value) }
            add(timeUnit.name)
            toleranceMs?.let { add(it) }
            maxAttempts?.let { add(it) }
        }
        val result = transport.sendRequest("blockViewer_timeDurationRate", params)
        return parseBlockRate(result)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseBlockList(result: Any?): List<SignedHydratedBlockWithHashMeta> {
        // TODO: full deserialization with Moshi adapters
        return emptyList()
    }

    private fun parseBlock(result: Any?): SignedHydratedBlockWithHashMeta? {
        // TODO: full deserialization with Moshi adapters
        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseBlockRate(result: Any?): BlockRate {
        val map = result as? Map<String, Any> ?: error("Invalid BlockRate response")
        val rangeList = map["range"] as? List<Number> ?: error("Invalid range in BlockRate")
        return BlockRate(
            range = XL1BlockRange(
                XL1BlockNumber(rangeList[0].toLong()),
                XL1BlockNumber(rangeList[1].toLong()),
            ),
            rate = (map["rate"] as Number).toDouble(),
            timeUnit = TimeUnit.valueOf(map["timeUnit"] as String),
            span = (map["span"] as Number).toLong(),
            timeDifference = (map["timeDifference"] as Number).toDouble(),
            timePerBlock = (map["timePerBlock"] as Number).toDouble(),
        )
    }
}
