package network.xyo.chain.protocol.rpc.engine

import network.xyo.chain.protocol.block.XL1BlockNumber
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
    RpcMethodNames.BLOCK_VIEWER_PAYLOADS_BY_HASH to RpcMethodHandler { params ->
        @Suppress("UNCHECKED_CAST")
        val hashes = params[0] as List<String>
        viewer.payloadsByHash(hashes)
    },
)
