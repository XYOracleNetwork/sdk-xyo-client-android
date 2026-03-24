package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.provider.Provider

typealias ForkHistory = Map<Long, ChainId>

interface ForkViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun forkHistory(): ForkHistory

    companion object {
        const val MONIKER = "ForkViewer"
    }
}
