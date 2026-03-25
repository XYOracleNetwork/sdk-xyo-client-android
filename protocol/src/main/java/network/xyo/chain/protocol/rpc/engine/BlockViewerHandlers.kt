package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.model.TimeUnit
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.BlockViewer

fun rpcMethodHandlersFromBlockViewer(viewer: BlockViewer): RpcMethodHandlerMap = mapOf(
    RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_HASH to RpcMethodHandler { params ->
        val hash = params[0] as String
        val limit = (params.getOrNull(1) as? Number)?.toInt()
        viewer.blocksByHash(hash, limit)
    },
    RpcMethodNames.BLOCK_VIEWER_BLOCKS_BY_NUMBER to RpcMethodHandler { params ->
        val block = XL1BlockNumber((params[0] as Number).toLong())
        val limit = (params.getOrNull(1) as? Number)?.toInt()
        viewer.blocksByNumber(block, limit)
    },
    RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK to RpcMethodHandler { viewer.currentBlock() },
    RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_HASH to RpcMethodHandler { viewer.currentBlockHash() },
    RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK_NUMBER to RpcMethodHandler { viewer.currentBlockNumber() },
    RpcMethodNames.BLOCK_VIEWER_CHAIN_ID to RpcMethodHandler { params ->
        val blockNumber = (params.getOrNull(0) as? Number)?.toLong()?.let { XL1BlockNumber(it) }
        viewer.chainId(blockNumber)
    },
    RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val hashes = params[0] as List<String>
        viewer.payloadsByHash(hashes)
    },
    RpcMethodNames.BLOCK_VIEWER_RATE to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val rangeList = params[0] as List<Number>
        val range = XL1BlockRange(XL1BlockNumber(rangeList[0].toLong()), XL1BlockNumber(rangeList[1].toLong()))
        val timeUnit = TimeUnit.valueOf(params[1] as String)
        viewer.rate(range, timeUnit)
    },
)
