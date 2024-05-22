package network.xyo.boundwitness

import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import network.xyo.Bytes
import network.xyo.account.Account
import network.xyo.payload.JSONPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith

val testVectorPrivate = "7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"

var TestPayload1 = JSONPayload("network.xyo.test", mapOf(
    "timestamp" to 1618603439107,
    "number_field" to "1",
    "object_field" to mapOf(
        "number_value" to 2,
        "string_value" to "yo"
    ),
    "string_field" to "there"
))

var TestPayload2 = JSONPayload("network.xyo.test", mapOf(
    "timestamp" to 1618603439108,
    "number_field" to "2",
    "object_field" to mapOf(
        "number_value" to 3,
        "string_value" to "yo_yo"
    ),
    "string_field" to "hello"
))

@RunWith(AndroidJUnit4::class)
class PayloadTest {
    @Test
    fun testBoundWitness() {
        val payloads = setOf(TestPayload1, TestPayload2)
        val account = Account(Bytes.hexToBytes(testVectorPrivate))
        val builder = BoundWitnessBuilder()
        builder.signer(account)
        builder.payloads(payloads)
        val bw = builder.build()
        val signatures = bw._meta?.getJSONArray("signatures")
        assertNotNull(bw._meta)
        assertNotNull(signatures)
        if (signatures !== null) {
            assertNotNull(signatures[0])
        }
        assertEquals(bw.addresses[0], account.address.hex)
    }
}