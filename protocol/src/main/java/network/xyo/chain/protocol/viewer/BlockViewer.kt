package network.xyo.chain.protocol.viewer

import network.xyo.chain.protocol.block.SignedHydratedBlockWithHashMeta
import network.xyo.chain.protocol.block.XL1BlockNumber
import network.xyo.chain.protocol.chain.ChainId
import network.xyo.chain.protocol.model.BlockRate
import network.xyo.chain.protocol.model.TimeConfig
import network.xyo.chain.protocol.model.TimeUnit
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.provider.Provider
import network.xyo.client.payload.model.Payload

interface BlockViewer : Provider {
    override val moniker: String get() = MONIKER

    suspend fun blockByHash(hash: String): SignedHydratedBlockWithHashMeta?
    suspend fun blockByNumber(block: XL1BlockNumber): SignedHydratedBlockWithHashMeta?
    suspend fun blocksByHash(hash: String, limit: Int? = null): List<SignedHydratedBlockWithHashMeta>
    suspend fun blocksByNumber(block: XL1BlockNumber, limit: Int? = null): List<SignedHydratedBlockWithHashMeta>
    suspend fun currentBlock(): SignedHydratedBlockWithHashMeta
    suspend fun currentBlockHash(): String
    suspend fun currentBlockNumber(): XL1BlockNumber
    suspend fun chainId(blockNumber: XL1BlockNumber? = null): ChainId
    suspend fun payloadByHash(hash: String): Payload?
    suspend fun payloadsByHash(hashes: List<String>): List<Payload>
    suspend fun rate(range: XL1BlockRange, timeUnit: TimeUnit = TimeUnit.seconds): BlockRate
    suspend fun stepSizeRate(start: XL1BlockNumber, stepIndex: Int, count: Int? = null, timeUnit: TimeUnit = TimeUnit.seconds): BlockRate
    suspend fun timeDurationRate(
        timeConfig: TimeConfig,
        startBlockNumber: XL1BlockNumber? = null,
        timeUnit: TimeUnit = TimeUnit.seconds,
        toleranceMs: Long? = null,
        maxAttempts: Int? = null,
    ): BlockRate

    companion object {
        const val MONIKER = "BlockViewer"
    }
}
