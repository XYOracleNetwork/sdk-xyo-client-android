package network.xyo.chain.protocol.sdk.transaction

import kotlinx.coroutines.runBlocking
import network.xyo.chain.protocol.transaction.TransactionFeesBigInt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

class TransactionBuilderTest {

    private val defaultFees = TransactionFeesBigInt(
        base = BigInteger.ZERO,
        gasLimit = BigInteger.valueOf(21000),
        gasPrice = BigInteger.ONE,
        priority = BigInteger.ZERO,
    )

    // Note: Tests that call build() with actual Account signers require Android
    // instrumented tests because BoundWitnessBuilder uses org.json.JSONObject
    // for payload hashing/signing. These unit tests validate builder config logic.

    @Test
    fun `throws when chain not set`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                TransactionBuilder()
                    .from("addr")
                    .fees(defaultFees)
                    .range(0L, 100L)
                    .build()
            }
        }
    }

    @Test
    fun `throws when fees not set`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                TransactionBuilder()
                    .chain("chain")
                    .from("addr")
                    .range(0L, 100L)
                    .build()
            }
        }
    }

    @Test
    fun `throws when range not set`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                TransactionBuilder()
                    .chain("chain")
                    .from("addr")
                    .fees(defaultFees)
                    .build()
            }
        }
    }

    @Test
    fun `builder methods are chainable`() {
        val builder = TransactionBuilder()
            .chain("chain_id")
            .from("from_addr")
            .fees(defaultFees)
            .range(100L, 200L)
            .script("transfer")

        assertNotNull(builder)
    }

    @Test
    fun `fees toHex produces correct values`() {
        val hex = defaultFees.toHex()
        assertEquals("0x0", hex.base)
        assertEquals("0x5208", hex.gasLimit)
        assertEquals("0x1", hex.gasPrice)
        assertEquals("0x0", hex.priority)
    }
}
