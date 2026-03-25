package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.provider.Provider
import network.xyo.client.payload.model.Payload

interface BlockViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun blockByHash(hash: String): SignedHydratedBlockWithHashMeta?
    suspend fun blockByNumber(block: XL1BlockNumber): SignedHydratedBlockWithHashMeta?
    suspend fun blocksByHash(hash: String, limit: Int? = null): List<SignedHydratedBlockWithHashMeta>
    suspend fun blocksByNumber(block: XL1BlockNumber, limit: Int? = null): List<SignedHydratedBlockWithHashMeta>
    suspend fun currentBlock(): SignedHydratedBlockWithHashMeta
    suspend fun payloadsByHash(hashes: List<String>): List<Payload>

    companion object {
        const val MONIKER = "BlockViewer"
    }
}
