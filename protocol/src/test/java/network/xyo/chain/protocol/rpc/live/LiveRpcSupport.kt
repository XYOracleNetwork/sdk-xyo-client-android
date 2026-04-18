package network.xyo.chain.protocol.rpc.live

import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport

/**
 * Shared support for live-compat tests that POST to a locally-running JS
 * JSON-RPC server (see xl1-compat/start-server.mjs).
 *
 * Tests in this package are gated by the `XL1_RPC_URL` env var. When unset
 * (the common case during a plain `./gradlew test`), JUnit 5's
 * `@EnabledIfEnvironmentVariable` annotation skips them. CI — and the
 * convenience `protocolLiveCompatTest` Gradle task — set it to point at the
 * freshly-started stub server.
 *
 * Expected stub values come from xl1-compat/stubs.mjs — head block 200000,
 * chain id c5fe2e6f..., stub epoch base 1_700_000_000_000 ms, 60_000 ms per
 * block, so head-epoch is 1_712_000_000_000 ms.
 */
internal object LiveRpcSupport {
    const val HEAD_BLOCK_NUMBER = 200_000L
    const val MS_PER_BLOCK = 60_000L
    const val BASE_EPOCH_MS = 1_700_000_000_000L
    const val HEAD_EPOCH_MS = BASE_EPOCH_MS + HEAD_BLOCK_NUMBER * MS_PER_BLOCK
    const val FINALIZED_BLOCK_NUMBER = HEAD_BLOCK_NUMBER - 6
    const val STUB_ADDRESS = "f93c0cff2245a7776792efeb4b229044cea4ec06"
    const val CHAIN_ID = "c5fe2e6f6841cbab12d8c0618be2df8c6156cc44"

    /** Hex-encoded 32-byte block hash. Stub uses `blockNumber.toString(16)` padded. */
    fun expectedBlockHash(blockNumber: Long): String =
        blockNumber.toString(16).padStart(64, '0')

    fun transport(): HttpRpcTransport {
        val url = System.getenv("XL1_RPC_URL") ?: error("XL1_RPC_URL not set")
        return HttpRpcTransport(url)
    }
}
