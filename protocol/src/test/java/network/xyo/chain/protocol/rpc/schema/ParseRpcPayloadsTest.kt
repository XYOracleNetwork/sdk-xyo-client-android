package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.payload.elevatable.TimePayload
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParseRpcPayloadsTest {

    @Test
    fun `parses valid TimePayload from RPC response`() {
        val rawPayloads = listOf(
            mapOf(
                "schema" to TimePayload.SCHEMA,
                "epoch" to 1700000000000L,
                "xl1" to 200000L,
            )
        )

        val parsed = parseRpcPayloads(rawPayloads)
        assertEquals(1, parsed.size)
        assertTrue(parsed.first() is TimePayload)
        val timePayload = parsed.first() as TimePayload
        assertEquals(1700000000000L, timePayload.epoch)
        assertEquals(200000L, timePayload.xl1)
    }

    @Test
    fun `handles malformed TimePayload gracefully without throwing JsonDataException`() {
        // Missing required 'epoch' field in TimePayload
        val malformedPayloads = listOf(
            mapOf(
                "schema" to TimePayload.SCHEMA,
                "xl1" to 200000L,
            )
        )

        val parsed = parseRpcPayloads(malformedPayloads)
        assertEquals(1, parsed.size)
        // Falls back to base Payload with the schema rather than crashing
        assertFalse(parsed.first() is TimePayload)
        assertEquals(TimePayload.SCHEMA, parsed.first().schema)
    }

    @Test
    fun `parses unknown schema to generic Payload`() {
        val unknownPayloads = listOf(
            mapOf(
                "schema" to "custom.unknown.payload",
                "foo" to "bar",
            )
        )

        val parsed = parseRpcPayloads(unknownPayloads)
        assertEquals(1, parsed.size)
        assertEquals("custom.unknown.payload", parsed.first().schema)
    }
}
