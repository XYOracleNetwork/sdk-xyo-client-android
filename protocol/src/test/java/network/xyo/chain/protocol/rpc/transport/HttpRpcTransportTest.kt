package network.xyo.chain.protocol.rpc.transport

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HttpRpcTransportTest {

    @Test
    fun `creates with url`() {
        val transport = HttpRpcTransport("https://example.com/rpc")
        assertNotNull(transport)
    }

    @Test
    fun `RpcTransportException stores message`() {
        val ex = RpcTransportException("test error")
        assertEquals("test error", ex.message)
    }

    @Test
    fun `RpcTransportException stores cause`() {
        val cause = RuntimeException("root cause")
        val ex = RpcTransportException("wrapped", cause)
        assertEquals(cause, ex.cause)
    }
}
