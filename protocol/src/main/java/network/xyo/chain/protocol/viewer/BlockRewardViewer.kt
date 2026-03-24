package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.provider.Provider
import network.xyo.chain.protocol.xl1.AttoXL1

interface BlockRewardViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun allowedRewardForBlock(block: XL1BlockNumber): AttoXL1

    companion object {
        const val MONIKER = "BlockRewardViewer"
    }
}
