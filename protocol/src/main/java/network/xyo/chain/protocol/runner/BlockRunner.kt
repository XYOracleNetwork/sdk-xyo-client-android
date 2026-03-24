package network.xyo.chain.protocol.runner

import network.xyo.chain.protocol.block.SignedBlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.provider.Provider

interface BlockRunner : Provider {
    override val moniker: String get() = MONIKER

    suspend fun produceNextBlock(head: SignedBlockBoundWitnessWithHashMeta, force: Boolean = false): SignedHydratedBlockWithHashMeta?

    companion object {
        const val MONIKER = "BlockRunner"
    }
}
