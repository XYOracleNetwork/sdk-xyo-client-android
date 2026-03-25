package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val TimeSyncViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Long>(RpcMethodNames.TIME_SYNC_VIEWER_SERVER_TIME) { raw ->
        (raw as Number).toLong()
    }
}
