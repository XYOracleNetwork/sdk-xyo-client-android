package network.xyo.chain.protocol.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PermissionTest {

    @Test
    fun `creates with required fields`() {
        val perm = Permission(
            parentCapability = "blockViewer_currentBlock",
            invoker = "invoker_addr",
        )
        assertEquals("blockViewer_currentBlock", perm.parentCapability)
        assertEquals("invoker_addr", perm.invoker)
        assertNull(perm.caveats)
    }

    @Test
    fun `creates with caveats`() {
        val perm = Permission(
            parentCapability = "blockViewer_currentBlock",
            invoker = "invoker_addr",
            caveats = listOf(
                Caveat(type = "chain", value = "chain_id"),
                Caveat(type = "expiration", value = 1000),
            ),
        )
        assertEquals(2, perm.caveats?.size)
        assertEquals("chain", perm.caveats!![0].type)
        assertEquals("expiration", perm.caveats!![1].type)
    }

    @Test
    fun `InvokerPermission has optional date`() {
        val perm = InvokerPermission(
            parentCapability = "test",
            invoker = "inv",
            date = 1234567890L,
        )
        assertEquals(1234567890L, perm.date)
    }

    @Test
    fun `RequestedPermission has optional date`() {
        val rp = RequestedPermission(parentCapability = "test")
        assertNull(rp.date)
    }
}
