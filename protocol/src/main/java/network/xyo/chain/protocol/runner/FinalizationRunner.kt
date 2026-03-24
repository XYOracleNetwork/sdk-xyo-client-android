package network.xyo.chain.protocol.runner

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.provider.Provider

interface FinalizationRunner : Provider {
    override val moniker: String get() = MONIKER

    suspend fun finalizeBlock(block: SignedHydratedBlockWithHashMeta): String
    suspend fun finalizeBlocks(blocks: List<SignedHydratedBlockWithHashMeta>): List<String>

    companion object {
        const val MONIKER = "FinalizationRunner"
    }
}
