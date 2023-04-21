package network.xyo.client

import network.xyo.client.node.client.QueryResponseWrapper
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class QueryResponseWrapperTest {
    @Test
    fun parseTest() {
        val queryResponseWrapper = QueryResponseWrapper.parse(TestConstants.queryResponseJson)
        assertNotEquals(queryResponseWrapper?.bw, null)
        assertEquals(queryResponseWrapper?.bwHash, TestConstants.queryResponseBWHash)
        assertNotEquals(queryResponseWrapper?.payloads?.size, 2)
    }
}