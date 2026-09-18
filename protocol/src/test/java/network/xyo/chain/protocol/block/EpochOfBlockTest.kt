package network.xyo.chain.protocol.block

import network.xyo.chain.protocol.payload.elevatable.TimePayload
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EpochOfBlockTest {

    @Test
    fun `prefers signed time payload over unsigned epoch meta`() {
        val block = HydratedBlock(
            boundWitness = BlockBoundWitness(
                block = 1L, chain = "abc123", previous = null, epoch = 5L,
            ),
            payloads = listOf(TimePayload(epoch = 9L)),
        )
        assertEquals(9L, epochOfBlock(block))
    }

    @Test
    fun `falls back to unsigned epoch for blocks that predate the time payload`() {
        val block = HydratedBlock(
            boundWitness = BlockBoundWitness(
                block = 1L, chain = "abc123", previous = null, epoch = 5L,
            ),
            payloads = emptyList(),
        )
        assertEquals(5L, epochOfBlock(block))
    }

    @Test
    fun `returns null when the block carries no clock`() {
        val block = HydratedBlock(
            boundWitness = BlockBoundWitness(
                block = 1L, chain = "abc123", previous = null,
            ),
            payloads = emptyList(),
        )
        assertNull(epochOfBlock(block))
    }
}
