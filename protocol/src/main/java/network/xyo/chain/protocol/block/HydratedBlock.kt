package network.xyo.chain.protocol.block

import network.xyo.chain.protocol.payload.elevatable.TimePayload
import network.xyo.client.payload.model.Payload

data class HydratedBlock(
    val boundWitness: BlockBoundWitness,
    val payloads: List<Payload>,
)

data class SignedHydratedBlock(
    val boundWitness: SignedBlockBoundWitness,
    val payloads: List<Payload>,
)

data class SignedHydratedBlockWithHashMeta(
    val boundWitness: SignedBlockBoundWitness,
    val payloads: List<Payload>,
    val hash: String,
)

/** The signed `network.xyo.time` payload a hydrated block carries, when it has one. */
fun timePayloadOf(payloads: List<Payload>): TimePayload? {
    return payloads.filterIsInstance<TimePayload>().firstOrNull()
}

/**
 * The epoch (ms) a block claims, read from its signed time payload.
 *
 * Blocks that predate the time payload carry only the unsigned `$epoch` client meta, which is
 * used as a fallback so reads over chain history keep working; anything that gates on the answer
 * should insist on the signed payload rather than accept the fallback.
 */
fun epochOfBlock(boundWitnessEpoch: Long?, payloads: List<Payload>): Long? {
    return timePayloadOf(payloads)?.epoch ?: boundWitnessEpoch
}

fun epochOfBlock(block: HydratedBlock): Long? = epochOfBlock(block.boundWitness.epoch, block.payloads)

fun epochOfBlock(block: SignedHydratedBlock): Long? = epochOfBlock(block.boundWitness.epoch, block.payloads)

fun epochOfBlock(block: SignedHydratedBlockWithHashMeta): Long? =
    epochOfBlock(block.boundWitness.epoch, block.payloads)

