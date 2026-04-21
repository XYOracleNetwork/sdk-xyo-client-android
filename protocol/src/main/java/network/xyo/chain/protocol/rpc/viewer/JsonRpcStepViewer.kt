package network.xyo.chain.protocol.rpc.viewer

import network.xyo.chain.protocol.model.Position
import network.xyo.chain.protocol.model.PositionId
import network.xyo.chain.protocol.rpc.schema.StepViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.RpcTransport
import network.xyo.chain.protocol.rpc.transport.sendRequest
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.PagedPositionsOptions
import network.xyo.chain.protocol.viewer.PagedStakersOptions
import network.xyo.chain.protocol.viewer.StepViewer
import java.math.BigInteger

class JsonRpcStepViewer(
    private val transport: RpcTransport,
) : StepViewer {
    override val moniker: String = StepViewer.MONIKER

    private val schemas = StepViewerRpcSchemas

    override suspend fun positionCount(step: Int): Int {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_POSITION_COUNT, listOf(step), schemas)
    }

    override suspend fun positions(step: Int, options: PagedPositionsOptions?): List<Position> {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_POSITIONS, listOfNotNull(step, options), schemas)
    }

    override suspend fun randomizer(step: Int): BigInteger {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_RANDOMIZER, listOf(step), schemas)
    }

    override suspend fun stake(step: Int): BigInteger {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_STAKE, listOf(step), schemas)
    }

    override suspend fun stakerCount(step: Int): Int {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_STAKER_COUNT, listOf(step), schemas)
    }

    override suspend fun stakers(step: Int, options: PagedStakersOptions?): List<String> {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_STAKERS, listOfNotNull(step, options), schemas)
    }

    override suspend fun weight(step: Int, positionId: PositionId?): BigInteger {
        return transport.sendRequest(RpcMethodNames.STEP_VIEWER_WEIGHT, listOfNotNull(step, positionId), schemas)
    }
}
