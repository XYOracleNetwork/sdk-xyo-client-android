package network.xyo.chain.protocol.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TransferTest {

    @Test
    fun `creates with required fields`() {
        val transfer = Transfer(
            from = "sender_addr",
            transfers = mapOf("recipient_addr" to "0xff"),
            epoch = 42L,
        )
        assertEquals("sender_addr", transfer.from)
        assertEquals(1, transfer.transfers.size)
        assertEquals("0xff", transfer.transfers["recipient_addr"])
        assertEquals(42L, transfer.epoch)
    }

    @Test
    fun `schema defaults to network xyo transfer`() {
        val transfer = Transfer(from = "a", transfers = emptyMap(), epoch = 0L)
        assertEquals("network.xyo.transfer", transfer.schema)
    }

    @Test
    fun `context defaults to null`() {
        val transfer = Transfer(from = "a", transfers = emptyMap(), epoch = 0L)
        assertNull(transfer.context)
    }

    @Test
    fun `multiple transfers supported`() {
        val transfer = Transfer(
            from = "sender",
            transfers = mapOf(
                "addr1" to "0x100",
                "addr2" to "0x200",
                "addr3" to "0x300",
            ),
            epoch = 1L,
        )
        assertEquals(3, transfer.transfers.size)
    }
}
