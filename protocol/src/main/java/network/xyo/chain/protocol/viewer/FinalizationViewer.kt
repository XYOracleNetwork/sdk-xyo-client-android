package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.SignedBlockBoundWitnessWithHashMeta
import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.provider.Provider

interface FinalizationViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun head(): SignedHydratedBlockWithHashMeta
    suspend fun headBlock(): SignedBlockBoundWitnessWithHashMeta
    suspend fun headHash(): String
    suspend fun headNumber(): XL1BlockNumber
    suspend fun chainId(): ChainId

    companion object {
        const val MONIKER = "FinalizationViewer"
    }
}
