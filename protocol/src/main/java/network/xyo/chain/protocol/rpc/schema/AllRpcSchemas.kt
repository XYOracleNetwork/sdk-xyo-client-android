package network.xyo.chain.protocol.rpc.schema

/** Aggregated upstream-compatible RPC schema map, mirroring xl1-protocol's AllRpcSchemas. */
val AllRpcSchemas: RpcSchemaMap = buildMap {
    putAll(AccountBalanceViewerRpcSchemas)
    putAll(BlockViewerRpcSchemas)
    putAll(DataLakeViewerRpcSchemas)
    putAll(FinalizationViewerRpcSchemas)
    putAll(MempoolViewerRpcSchemas)
    putAll(MempoolRunnerRpcSchemas)
    putAll(NetworkStakeViewerRpcSchemas)
    putAll(TimeSyncViewerRpcSchemas)
    putAll(StepViewerRpcSchemas)
    putAll(StakeTotalsViewerRpcSchemas)
    putAll(StakeViewerRpcSchemas)
    putAll(NetworkStakingStepRewardsViewerRpcSchemas)
    putAll(NetworkStakingStepRewardsTotalViewerRpcSchemas)
    putAll(NetworkStakingStepRewardsByStepViewerRpcSchemas)
    putAll(NetworkStakingStepRewardsByStakerViewerRpcSchemas)
    putAll(NetworkStakingStepRewardsByPositionViewerRpcSchemas)
    putAll(TransactionViewerRpcSchemas)
}
