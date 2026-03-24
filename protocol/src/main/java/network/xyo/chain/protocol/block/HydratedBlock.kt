package network.xyo.chain.protocol.block

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
