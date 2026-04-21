package network.xyo.chain.protocol.rpc.viewer

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.model.AccountBalanceConfig
import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.rpc.schema.RpcSchema
import network.xyo.chain.protocol.rpc.schema.AccountBalanceViewerRpcSchemas
import network.xyo.chain.protocol.rpc.transport.HttpRpcTransport
import network.xyo.chain.protocol.rpc.schema.rpcMoshi
import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.chain.protocol.block.XL1BlockNumber
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonRpcAccountBalanceViewerTest {

    private val transport = HttpRpcTransport("https://beta.api.chain.xyo.network/rpc")
    private val viewer = JsonRpcAccountBalanceViewer(transport)

    private val testAddress = "0000000000000000000000000000000000000000"

    @Test
    fun `qualified balance schema parses tuple wire format`() {
        val raw = listOf(
            mapOf(testAddress to "0x2a"),
            mapOf("head" to "abcd1234", "range" to listOf(100L, 110L)),
        )

        @Suppress("UNCHECKED_CAST")
        val schema = AccountBalanceViewerRpcSchemas["accountBalanceViewer_qualifiedAccountBalances"] as RpcSchema<ChainQualified<Map<String, AttoXL1>>>

        val parsed = schema.parseResult(rpcMoshi, raw)

        assertEquals(AttoXL1.of(java.math.BigInteger.valueOf(42)), parsed.data[testAddress])
        assertEquals("abcd1234", parsed.qualification.head)
        assertEquals(XL1BlockRange(XL1BlockNumber(100), XL1BlockNumber(110)), parsed.qualification.range)
    }

    @Test
    fun `qualified balance schema parses bare hex strings`() {
        val raw = listOf(
            mapOf(testAddress to "8ac7230"),
            mapOf("head" to "abcd1234", "range" to listOf(100L, 110L)),
        )

        @Suppress("UNCHECKED_CAST")
        val schema = AccountBalanceViewerRpcSchemas["accountBalanceViewer_qualifiedAccountBalances"] as RpcSchema<ChainQualified<Map<String, AttoXL1>>>

        val parsed = schema.parseResult(rpcMoshi, raw)

        assertEquals(AttoXL1.of(java.math.BigInteger("8ac7230", 16)), parsed.data[testAddress])
    }

    @Test
    fun `qualified histories schema parses tuple wire format`() {
        val raw = listOf(
            mapOf(testAddress to emptyList<Any>()),
            mapOf("head" to "abcd1234", "range" to listOf(100L, 110L)),
        )

        @Suppress("UNCHECKED_CAST")
        val schema = AccountBalanceViewerRpcSchemas["accountBalanceViewer_qualifiedAccountBalanceHistories"] as RpcSchema<ChainQualified<Map<String, List<AccountBalanceHistoryItem>>>>

        val parsed = schema.parseResult(rpcMoshi, raw)

        assertEquals(emptyList<AccountBalanceHistoryItem>(), parsed.data[testAddress])
        assertEquals("abcd1234", parsed.qualification.head)
        assertEquals(XL1BlockRange(XL1BlockNumber(100), XL1BlockNumber(110)), parsed.qualification.range)
    }

    @Test
    fun `qualifiedAccountBalances reaches the server`() {
        val result = runBlocking {
            viewer.qualifiedAccountBalances(listOf(testAddress), AccountBalanceConfig())
        }

        assertFalse(result.qualification.head.isBlank())
        assertTrue(result.qualification.range.start <= result.qualification.range.end)
    }
}
