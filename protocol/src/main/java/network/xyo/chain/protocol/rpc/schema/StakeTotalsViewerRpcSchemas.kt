package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import java.math.BigInteger

val StakeTotalsViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKED) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_ACTIVE_BY_STAKER) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_PENDING) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_PENDING_BY_STAKER) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_WITHDRAWN) { raw -> BigInteger(raw.toString()) }
    method<BigInteger>(RpcMethodNames.STAKE_TOTALS_VIEWER_WITHDRAWN_BY_STAKER) { raw -> BigInteger(raw.toString()) }
}
