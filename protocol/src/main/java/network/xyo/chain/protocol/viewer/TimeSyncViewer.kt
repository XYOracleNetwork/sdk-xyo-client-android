package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.provider.Provider

interface TimeSyncViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun serverTime(): Long

    companion object {
        const val MONIKER = "TimeSyncViewer"
    }
}
