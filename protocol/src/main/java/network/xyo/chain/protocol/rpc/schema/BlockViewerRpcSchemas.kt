package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.model.BlockRate
import network.xyo.chain.protocol.model.TimeUnit
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.client.payload.model.Payload

val BlockViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<List<SignedHydratedBlockWithHashMeta>>(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_HASH)
    method<List<SignedHydratedBlockWithHashMeta>>(RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_NUMBER)
    method<SignedHydratedBlockWithHashMeta>(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK)
    method<String>(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_HASH)
    method<XL1BlockNumber>(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_NUMBER) { raw ->
        XL1BlockNumber((raw as Number).toLong())
    }
    method<ChainId>(RpcMethodNames.BLOCK_VIEWER_CHAIN_ID) { raw ->
        raw as? String ?: error("Chain ID not found")
    }
    method<List<Payload>>(RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH)
    method<BlockRate>(RpcMethodNames.BLOCK_VIEWER_RATE) { raw ->
        parseBlockRate(raw)
    }
}

@Suppress("UNCHECKED_CAST")
private fun parseBlockRate(raw: Any?): BlockRate {
    val map = raw as? Map<String, Any> ?: error("Invalid BlockRate response")
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
