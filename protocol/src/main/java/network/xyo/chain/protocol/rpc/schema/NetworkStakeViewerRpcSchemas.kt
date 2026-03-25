package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.viewer.ActiveStakeResult
import java.math.BigInteger

val NetworkStakeViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<ActiveStakeResult>(RpcMethodNames.NETWORK_STAKE_VIEWER_ACTIVE) { raw ->
        @Suppress("UNCHECKED_CAST")
        val tuple = raw as List<Any?>
        val stake = when (val v = tuple[0]) {
            is String -> BigInteger(v.removePrefix("0x"), 16)
            is Number -> BigInteger.valueOf(v.toLong())
            else -> BigInteger.ZERO
        }
        val count = (tuple[1] as Number).toInt()
        ActiveStakeResult(stake, count)
    }
}
