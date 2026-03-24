package network.xyo.chain.protocol.block

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BlockBoundWitnessTest {

    @Test
    fun `creates with required fields`() {
        val block = BlockBoundWitness(
            block = 42L,
            chain = "abc123",
            previous = "prevhash",
            epoch = 1000L,
        )
        assertEquals(42L, block.block)
        assertEquals("abc123", block.chain)
        assertEquals("prevhash", block.previous)
        assertEquals(1000L, block.epoch)
    }

    @Test
    fun `blockNumber converts correctly`() {
        val block = BlockBoundWitness(
            block = 42L, chain = "abc123", previous = null, epoch = 1000L,
        )
        assertEquals(XL1BlockNumber(42L), block.blockNumber)
    }

    @Test
    fun `previous can be null for genesis block`() {
        val block = BlockBoundWitness(
            block = 0L, chain = "abc123", previous = null, epoch = 0L,
        )
        assertNull(block.previous)
    }

    @Test
    fun `schema has correct default`() {
        val block = BlockBoundWitness(
            block = 0L, chain = "abc123", previous = null, epoch = 0L,
        )
        assertEquals("network.xyo.boundwitness", block.schema)
    }

    @Test
    fun `optional fields default correctly`() {
        val block = BlockBoundWitness(
            block = 0L, chain = "abc123", previous = null, epoch = 0L,
        )
        assertNull(block.protocol)
        assertNull(block.step_hashes)
        assertNull(block.signatures)
        assertEquals(emptyList<String>(), block.addresses)
        assertEquals(emptyList<String>(), block.payload_hashes)
    }

    @Test
    fun `step_hashes can be set`() {
        val block = BlockBoundWitness(
            block = 0L, chain = "abc123", previous = null, epoch = 0L,
            step_hashes = listOf("hash1", "hash2"),
        )
        assertEquals(listOf("hash1", "hash2"), block.step_hashes)
    }
}
