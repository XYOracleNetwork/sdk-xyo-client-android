package network.xyo.client.node.client

import network.xyo.client.lib.TestConstants
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