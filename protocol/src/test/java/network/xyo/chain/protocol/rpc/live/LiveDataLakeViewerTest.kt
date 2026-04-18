package network.xyo.chain.protocol.rpc.live

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.rpc.viewer.JsonRpcDataLakeViewer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

@EnabledIfEnvironmentVariable(named = "XL1_RPC_URL", matches = ".+")
class LiveDataLakeViewerTest {
    private val viewer = JsonRpcDataLakeViewer(LiveRpcSupport.transport())

    // Stub seeds two payloads keyed by these hashes — see xl1-compat/stubs.mjs.
    // Fields come through intact because the JS schema uses PayloadZod.loose().
    private val expected = mapOf(
        "5d185be39c900cd03ba18d4bfeb91ae1d00400749b19bbb7651ffe15771bfc97" to
            mapOf("schema" to "network.xyo.test", "value" to "hello"),
        "770b6ca959389c0da23ac969d1d6288c8e96542ce43570a652a2b1f5e4c9759d" to
            mapOf("schema" to "network.xyo.id", "salt" to "42"),
    )

    @Test
    fun `get returns seeded payloads with all fields preserved`() = runBlocking {
        val hashes = expected.keys.toList()
        val result = viewer.get(hashes)
        assertEquals(expected.size, result.size)
        val bySchema = result.associateBy { it["schema"] as String }
        val expectedBySchema = expected.values.associateBy { it["schema"] as String }
        assertEquals(expectedBySchema.keys, bySchema.keys)
        assertEquals(expectedBySchema["network.xyo.test"]!!["value"], bySchema["network.xyo.test"]!!["value"])
        assertEquals(expectedBySchema["network.xyo.id"]!!["salt"], bySchema["network.xyo.id"]!!["salt"])
    }

    @Test
    fun `get for unknown hash returns empty list`() = runBlocking {
        val result = viewer.get(listOf("0".repeat(64)))
        assertEquals(0, result.size)
    }

    @Test
    fun `next returns all seeded payloads with all fields preserved`() = runBlocking {
        val result = viewer.next()
        assertEquals(expected.size, result.size)
        val schemas = result.map { it["schema"] }.toSet()
        assertEquals(expected.values.map { it["schema"] }.toSet(), schemas)
        // Confirm the non-schema fields survived .loose() round-trip.
        val values = result.map { it["value"] ?: it["salt"] }.toSet()
        assertEquals(setOf("hello", "42"), values)
    }
}
