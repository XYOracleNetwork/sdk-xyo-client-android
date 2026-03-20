package network.xyo.client

import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoInvalidSchemaException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class PayloadTest {

    @Test
    fun `payload with valid lowercase schema validates`() {
        val payload = Payload("network.xyo.test")
        assertDoesNotThrow { payload.validate() }
    }

    @Test
    fun `payload with uppercase schema throws validation error`() {
        val payload = Payload("network.xyo.Test")
        assertThrows<XyoInvalidSchemaException> { payload.validate() }
    }

    @Test
    fun `payload schema is preserved`() {
        val payload = Payload("network.xyo.test")
        assertEquals("network.xyo.test", payload.schema)
    }

    @Test
    fun `payload hash is deterministic`() {
        val payload1 = Payload("network.xyo.test")
        val payload2 = Payload("network.xyo.test")
        assertEquals(payload1.hash().toList(), payload2.hash().toList())
    }

    @Test
    fun `payload dataHash is deterministic`() {
        val payload1 = Payload("network.xyo.test")
        val payload2 = Payload("network.xyo.test")
        assertEquals(payload1.dataHash().toList(), payload2.dataHash().toList())
    }

    @Test
    fun `different schemas produce different hashes`() {
        val payload1 = Payload("network.xyo.test.a")
        val payload2 = Payload("network.xyo.test.b")
        assert(payload1.hash().toList() != payload2.hash().toList())
    }
}
