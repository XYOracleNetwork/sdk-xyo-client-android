package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.rpc.types.RpcMethodNames

val TimeSyncViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<Long>(RpcMethodNames.TIME_SYNC_VIEWER_CONVERT_TIME) { raw ->
        (raw as Number).toLong()
    }
    method<List<Any?>>(RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME) { raw ->
        @Suppress("UNCHECKED_CAST")
        raw as List<Any?>
    }
    method<List<Any?>>(RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME_AND_HASH) { raw ->
        @Suppress("UNCHECKED_CAST")
        raw as List<Any?>
    }
    method<Map<String, Any?>>(RpcMethodNames.TIME_SYNC_VIEWER_CURRENT_TIME_PAYLOAD) { raw ->
        @Suppress("UNCHECKED_CAST")
        raw as Map<String, Any?>
    }
}
