package network.xyo.client.node.client

import network.xyo.client.lib.TestConstants
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalStdlibApi::class)
class QueryResponseWrapperTest {
    @Test
    fun parseTest() {
        val queryResponseWrapper = QueryResponseWrapper.parse(TestConstants.queryResponseJson)
        assertNotEquals(queryResponseWrapper?.bw, null)
        assertEquals(queryResponseWrapper?.bwHash?.toHexString(), TestConstants.queryResponseBWHash)

        assertNotEquals(queryResponseWrapper?.payloads?.size, 2)
    }
}