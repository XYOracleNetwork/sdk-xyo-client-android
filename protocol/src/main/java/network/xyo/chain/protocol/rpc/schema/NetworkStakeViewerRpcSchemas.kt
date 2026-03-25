package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import java.math.BigInteger

val NetworkStakeViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<BigInteger>(RpcMethodNames.NETWORK_STAKE_VIEWER_TOTAL_STAKE) { raw ->
        when (raw) {
            is String -> BigInteger(raw.removePrefix("0x"), 16)
            is Number -> BigInteger.valueOf(raw.toLong())
            else -> BigInteger.ZERO
        }
    }
    method<Int>(RpcMethodNames.NETWORK_STAKE_VIEWER_POSITION_COUNT) { raw ->
        (raw as Number).toInt()
    }
}
