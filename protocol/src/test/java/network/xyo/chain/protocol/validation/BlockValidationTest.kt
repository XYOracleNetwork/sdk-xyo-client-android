package network.xyo.chain.protocol.validation

import network.xyo.chain.protocol.block.BlockBoundWitness
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BlockValidationTest {

    private fun validBlock() = BlockBoundWitness(
        block = 42L,
        chain = "chain_id",
        previous = "prev_hash",
        epoch = 1000L,
        addresses = listOf("addr1"),
        previous_hashes = listOf(null),
    )

    @Test
    fun `passes for valid block`() {
        val errors = BlockCumulativeBalanceValidator().validate(validBlock())
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `catches negative block number`() {
        val block = validBlock().copy(block = -1L)
        val errors = BlockCumulativeBalanceValidator().validate(block)
        assertTrue(errors.any { it.code == "INVALID_BLOCK_NUMBER" })
    }

    @Test
    fun `catches blank chain`() {
        val block = validBlock().copy(chain = "")
        val errors = BlockCumulativeBalanceValidator().validate(block)
        assertTrue(errors.any { it.code == "MISSING_CHAIN" })
    }

    @Test
    fun `catches missing addresses`() {
        val block = validBlock().copy(addresses = emptyList())
        val errors = BlockCumulativeBalanceValidator().validate(block)
        assertTrue(errors.any { it.code == "MISSING_ADDRESSES" })
    }

    @Test
    fun `validateBlock runs all validators`() {
        val block = validBlock()
        val errors = validateBlock(block, listOf(BlockCumulativeBalanceValidator()))
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateBlock includes sdk bound witness validation`() {
        val block = validBlock().copy(previous_hashes = emptyList())
        val errors = validateBlock(block, emptyList())
        assertTrue(errors.any { it.code == "PREVIOUS_HASH_MISMATCH" })
    }
}
