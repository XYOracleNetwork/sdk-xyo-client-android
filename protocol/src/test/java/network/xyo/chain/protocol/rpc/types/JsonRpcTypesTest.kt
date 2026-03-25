package network.xyo.chain.protocol.rpc.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonRpcTypesTest {

    @Test
    fun `JsonRpcRequest has correct defaults`() {
        val req = JsonRpcRequest(id = "1", method = "test_method")
        assertEquals("2.0", req.jsonrpc)
        assertEquals("1", req.id)
        assertEquals("test_method", req.method)
    }

    @Test
    fun `JsonRpcRequest params default to empty list`() {
        val req = JsonRpcRequest(id = "1", method = "test")
        assertEquals(emptyList<Any>(), req.params)
    }

    @Test
    fun `JsonRpcResponse isSuccess when no error`() {
        val res = JsonRpcResponse(id = "1", result = "value")
        assertTrue(res.isSuccess)
        assertFalse(res.isError)
    }

    @Test
    fun `JsonRpcResponse isError when error present`() {
        val res = JsonRpcResponse(
            id = "1",
            error = JsonRpcError(code = -32600, message = "Invalid Request"),
        )
        assertFalse(res.isSuccess)
        assertTrue(res.isError)
    }

    @Test
    fun `JsonRpcResponse result is null when error`() {
        val res = JsonRpcResponse(
            id = "1",
            error = JsonRpcError(code = -32600, message = "Invalid Request"),
        )
        assertNull(res.result)
    }

    @Test
    fun `JsonRpcErrorCodes have correct values`() {
        assertEquals(-32700, JsonRpcErrorCodes.PARSE_ERROR)
        assertEquals(-32600, JsonRpcErrorCodes.INVALID_REQUEST)
        assertEquals(-32601, JsonRpcErrorCodes.METHOD_NOT_FOUND)
        assertEquals(-32602, JsonRpcErrorCodes.INVALID_PARAMS)
        assertEquals(-32603, JsonRpcErrorCodes.INTERNAL_ERROR)
    }

    @Test
    fun `RpcMethodNames constants are non-empty`() {
        assertTrue(RpcMethodNames.BLOCK_VIEWER_CURRENT_BLOCK.isNotEmpty())
        assertTrue(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES.isNotEmpty())
        assertTrue(RpcMethodNames.MEMPOOL_RUNNER_SUBMIT_TRANSACTIONS.isNotEmpty())
    }
}
