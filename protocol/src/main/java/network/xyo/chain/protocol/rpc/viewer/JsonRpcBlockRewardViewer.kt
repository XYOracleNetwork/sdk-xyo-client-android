package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.rpc.schema.BlockRewardViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.BlockRewardViewer
import network.xyo.chain.protocol.xl1.AttoXL1

class JsonRpcBlockRewardViewer(
    private val transport: RpcTransport,
) : BlockRewardViewer {
    override val moniker: String = BlockRewardViewer.MONIKER

    private val schemas = BlockRewardViewerRpcSchemas

    override suspend fun allowedRewardForBlock(block: XL1BlockNumber): AttoXL1 {
        return transport.sendRequest(RpcMethodNames.BLOCK_REWARD_VIEWER_ALLOWED_REWARD, listOf(block.value), schemas)
    }
}
