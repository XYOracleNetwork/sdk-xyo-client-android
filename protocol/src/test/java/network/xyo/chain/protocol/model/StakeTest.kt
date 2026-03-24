package network.xyo.chain.protocol.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class StakeTest {

    @Test
    fun `Stake toJson converts amount to hex`() {
        val stake = Stake(
            amount = BigInteger.valueOf(255),
            addBlock = 100L,
            id = 1,
            removeBlock = 0L,
            staked = "staked_addr",
            staker = "staker_addr",
            withdrawBlock = 0L,
        )
        val json = stake.toJson()
        assertEquals("0xff", json.amount)
        assertEquals(100L, json.addBlock)
    }

    @Test
    fun `StakeJson toStake converts hex to BigInteger`() {
        val json = StakeJson(
            amount = "0xff",
            addBlock = 100L,
            id = 1,
            removeBlock = 0L,
            staked = "staked_addr",
            staker = "staker_addr",
            withdrawBlock = 0L,
        )
        val stake = json.toStake()
        assertEquals(BigInteger.valueOf(255), stake.amount)
    }

    @Test
    fun `round trip Stake to json and back`() {
        val original = Stake(
            amount = BigInteger.valueOf(1_000_000),
            addBlock = 100L,
            id = 42,
            removeBlock = 0L,
            staked = "staked",
            staker = "staker",
            withdrawBlock = 0L,
        )
        val roundTripped = original.toJson().toStake()
        assertEquals(original, roundTripped)
    }

    @Test
    fun `toPosition converts correctly`() {
        val stake = Stake(
            amount = BigInteger.valueOf(500),
            addBlock = 10L,
            id = 5,
            removeBlock = 0L,
            staked = "s1",
            staker = "s2",
            withdrawBlock = 0L,
        )
        val pos = stake.toPosition()
        assertEquals(stake.amount, pos.amount)
        assertEquals(stake.id, pos.id)
        assertEquals(stake.addBlock, pos.addBlock)
        assertEquals(stake.staked, pos.staked)
        assertEquals(stake.staker, pos.staker)
    }
}
