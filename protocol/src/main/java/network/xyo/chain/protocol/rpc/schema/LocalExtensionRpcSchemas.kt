package network.xyo.chain.protocol.rpc.schema

/**
 * Schema maps that are intentionally local extensions and do not currently
 * exist in xl1-protocol's authoritative `AllRpcSchemas`.
 */
val LocalExtensionRpcSchemas: RpcSchemaMap = buildMap {
    putAll(TransferBalanceViewerRpcSchemas)
    putAll(BlockRewardViewerRpcSchemas)
    putAll(StakeRunnerRpcSchemas)
    putAll(LegacyStepRewardsViewerRpcSchemas)
}

/**
 * Effective runtime registry for this Kotlin SDK. This includes the
 * authoritative upstream-compatible schemas plus local extensions that this
 * repository still exposes.
 */
val EngineAllRpcSchemas: RpcSchemaMap = buildMap {
    putAll(AllRpcSchemas)
    putAll(LocalExtensionRpcSchemas)
}
