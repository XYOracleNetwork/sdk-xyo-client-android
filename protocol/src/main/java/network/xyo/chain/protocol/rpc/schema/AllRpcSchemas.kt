package network.xyo.chain.protocol.rpc.schema

/** Aggregated map of all RPC method schemas, mirroring AllRpcSchemas from xl1-protocol. */
val AllRpcSchemas: RpcSchemaMap = buildMap {
    putAll(BlockViewerRpcSchemas)
    putAll(AccountBalanceViewerRpcSchemas)
    putAll(TransactionViewerRpcSchemas)
    putAll(StakeViewerRpcSchemas)
    putAll(FinalizationViewerRpcSchemas)
    putAll(TimeSyncViewerRpcSchemas)
    putAll(NetworkStakeViewerRpcSchemas)
    putAll(MempoolViewerRpcSchemas)
    putAll(MempoolRunnerRpcSchemas)
}
