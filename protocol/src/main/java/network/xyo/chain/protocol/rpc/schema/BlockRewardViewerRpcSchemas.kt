package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.xl1.AttoXL1
import java.math.BigInteger

val BlockRewardViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<AttoXL1>(RpcMethodNames.BLOCK_REWARD_VIEWER_ALLOWED_REWARD) { raw ->
        AttoXL1(BigInteger(raw.toString()))
    }
}
