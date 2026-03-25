package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.provider.Provider

interface FinalizationViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun head(): SignedHydratedBlockWithHashMeta

    companion object {
        const val MONIKER = "FinalizationViewer"
    }
}
