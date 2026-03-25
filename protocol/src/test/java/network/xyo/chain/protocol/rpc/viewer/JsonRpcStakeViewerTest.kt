package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import network.xyo.chain.protocol.rpc.transport.RpcTransportException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonRpcStakeViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcStakeViewer(transport)

    private val zeroAddress = "0000000000000000000000000000000000000000"

    @Test
    fun `minWithdrawalBlocks is not available on beta`() {
        assertThrows<RpcTransportException> {
            runBlocking { viewer.minWithdrawalBlocks() }
        }
    }

    @Test
    fun `rewardsContract is not available on beta`() {
        assertThrows<RpcTransportException> {
            runBlocking { viewer.rewardsContract() }
        }
    }

    @Test
    fun `stakingTokenAddress is not available on beta`() {
        assertThrows<RpcTransportException> {
            runBlocking { viewer.stakingTokenAddress() }
        }
    }

    @Test
    fun `stakesByStaker is not available on beta`() {
        assertThrows<RpcTransportException> {
            runBlocking { viewer.stakesByStaker(zeroAddress) }
        }
    }

    @Test
    fun `stakesByStaked is not available on beta`() {
        assertThrows<RpcTransportException> {
            runBlocking { viewer.stakesByStaked(zeroAddress) }
        }
    }
}
