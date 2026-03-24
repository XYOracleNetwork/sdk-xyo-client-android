package network.xyo.chain.protocol.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigInteger

class PositionTest {

    @Test
    fun `active position has removeBlock and withdrawBlock at 0`() {
        val pos = Position(
            addBlock = 100L,
            amount = BigInteger.valueOf(1000),
            id = 1,
            removeBlock = 0L,
            staked = "staked_addr",
            staker = "staker_addr",
            withdrawBlock = 0L,
        )
        assertTrue(pos.isActive)
        assertFalse(pos.isRemoved)
        assertFalse(pos.isWithdrawn)
    }

    @Test
    fun `removed position has removeBlock set but not withdrawBlock`() {
        val pos = Position(
            addBlock = 100L,
            amount = BigInteger.valueOf(1000),
            id = 1,
            removeBlock = 200L,
            staked = "staked_addr",
            staker = "staker_addr",
            withdrawBlock = 0L,
        )
        assertFalse(pos.isActive)
        assertTrue(pos.isRemoved)
        assertFalse(pos.isWithdrawn)
    }

    @Test
    fun `withdrawn position has withdrawBlock set`() {
        val pos = Position(
            addBlock = 100L,
            amount = BigInteger.valueOf(1000),
            id = 1,
            removeBlock = 200L,
            staked = "staked_addr",
            staker = "staker_addr",
            withdrawBlock = 300L,
        )
        assertFalse(pos.isActive)
        assertFalse(pos.isRemoved)
        assertTrue(pos.isWithdrawn)
    }
}
