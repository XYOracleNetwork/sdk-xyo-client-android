package network.xyo.chain.protocol.rpc.live

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.viewer.JsonRpcNetworkStakingStepRewardsByPositionViewer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.math.BigInteger

/**
 * Stub sets earned=1e18 and claimed=4e17, which produces:
 *   bonus     = earned / 10   = 1e17
 *   claimed   = 4e17
 *   earned    = 1e18
 *   total     = earned + bonus = 1.1e18
 *   unclaimed = earned - claimed = 6e17
 * All keyed by the stub position index 42.
 */
@EnabledIfEnvironmentVariable(named = "XL1_RPC_URL", matches = ".+")
class LiveRewardsByPositionViewerTest {
    private val viewer = JsonRpcNetworkStakingStepRewardsByPositionViewer(LiveRpcSupport.transport())

    private val POSITION_KEY = "42"
    private val ONE_XL1 = BigInteger.TEN.pow(18)
    private val HUNDRED_MILLI = ONE_XL1.divide(BigInteger.TEN)                 // 1e17
    private val FOUR_HUNDRED_MILLI = ONE_XL1.multiply(BigInteger.valueOf(4))   // 4e17
        .divide(BigInteger.TEN)
    private val SIX_HUNDRED_MILLI = ONE_XL1.multiply(BigInteger.valueOf(6))    // 6e17
        .divide(BigInteger.TEN)
    private val ONE_POINT_ONE_XL1 = ONE_XL1.add(HUNDRED_MILLI)                 // 1.1e18

    @Test
    fun `earned returns stub value keyed by position`() = runBlocking {
        val result = viewer.earned()
        assertEquals(ONE_XL1, result[POSITION_KEY])
    }

    @Test
    fun `claimed returns stub value`() = runBlocking {
        val result = viewer.claimed()
        assertEquals(FOUR_HUNDRED_MILLI, result[POSITION_KEY])
    }

    @Test
    fun `bonus is one-tenth of earned`() = runBlocking {
        val result = viewer.bonus()
        assertEquals(HUNDRED_MILLI, result[POSITION_KEY])
    }

    @Test
    fun `total is earned plus bonus`() = runBlocking {
        val result = viewer.total()
        assertEquals(ONE_POINT_ONE_XL1, result[POSITION_KEY])
    }

    @Test
    fun `unclaimed is earned minus claimed`() = runBlocking {
        val result = viewer.unclaimed()
        assertEquals(SIX_HUNDRED_MILLI, result[POSITION_KEY])
    }

    @Test
    fun `returned map has exactly one entry`() = runBlocking {
        val result = viewer.earned()
        assertTrue(result.size == 1, "expected one-entry map, got $result")
    }
}
