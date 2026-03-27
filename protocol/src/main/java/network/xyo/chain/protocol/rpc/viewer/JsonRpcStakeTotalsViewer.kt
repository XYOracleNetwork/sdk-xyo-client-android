package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.rpc.schema.StakeTotalsViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.StakeTotalsViewer
import java.math.BigInteger

class JsonRpcStakeTotalsViewer(
    private val transport: RpcTransport,
) : StakeTotalsViewer {
    override val moniker: String = StakeTotalsViewer.MONIKER

    private val schemas = StakeTotalsViewerRpcSchemas

    override suspend fun active(time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE, listOfNotNull(time), schemas)
    }

    override suspend fun activeByStaked(staked: String, time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKED, listOfNotNull(staked, time), schemas)
    }

    override suspend fun activeByStaker(address: String, time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKER, listOfNotNull(address, time), schemas)
    }

    override suspend fun pending(time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_PENDING, listOfNotNull(time), schemas)
    }

    override suspend fun pendingByStaker(staker: String, time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_PENDING_BY_STAKER, listOfNotNull(staker, time), schemas)
    }

    override suspend fun withdrawn(time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_WITHDRAWN, listOfNotNull(time), schemas)
    }

    override suspend fun withdrawnByStaker(staker: String, time: Long?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STAKE_TOTALS_VIEWER_WITHDRAWN_BY_STAKER, listOfNotNull(staker, time), schemas)
    }
}
