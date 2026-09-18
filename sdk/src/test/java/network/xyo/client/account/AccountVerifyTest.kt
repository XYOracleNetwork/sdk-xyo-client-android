package network.xyo.client.account

import kotlinx.coroutines.runBlocking
import network.xyo.client.boundwitness.BoundWitnessValidator
import network.xyo.client.lib.hexStringToByteArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger

class AccountVerifyTest {

    private val testVectorPrivateKey = "7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"
    private val testVectorAddress = "5e7a847447e7fec41011ae7d32d768f86605ba03"
    private val testVectorHash = "4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a"
    private val testVectorSignature = "b61dad551e910e2793b4f9f880125b5799086510ce102fad0222c1b093c60a6b38aa35ef56f97f86537269e8be95832aaa37d3b64d86b67f0cda467ac7cb5b3e"

    @Test
    fun `Account verify validates test vector signature`() {
        val account = Account.fromPrivateKey(hexStringToByteArray(testVectorPrivateKey))
        @OptIn(ExperimentalStdlibApi::class)
        assertEquals(testVectorAddress, account.address.toHexString())
        assertTrue(account.verify(hexStringToByteArray(testVectorHash), hexStringToByteArray(testVectorSignature)))
    }

    @Test
    fun `Account verify roundtrips on randomly generated accounts`() = runBlocking {
        repeat(5) {
            val account = Account.random()
            val msg = hexStringToByteArray("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
            val sig = account.sign(msg)
            assertTrue(account.verify(msg, sig), "Signature verification failed for random account")
            assertFalse(account.verify(hexStringToByteArray("deadbeefcafef00d000000000000000000000000000000000000000000000000"), sig))
        }
    }

    @Test
    fun `Account constructor with BigInteger handles high bit and low bit keys`() {
        // High bit set (0x80...)
        val highBitKey = BigInteger("8f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", 16)
        val acc1 = Account(highBitKey)
        assertEquals(32, acc1.privateKey.size)

        // Low bit key
        val lowBitKey = BigInteger("0071bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", 16)
        val acc2 = Account(lowBitKey)
        assertEquals(32, acc2.privateKey.size)
    }

    @Test
    fun `addressFromUncompressedPublicKey throws IllegalArgumentException when key is not 64 bytes`() {
        assertThrows<IllegalArgumentException> {
            Account.addressFromUncompressedPublicKey(ByteArray(63))
        }
        assertThrows<IllegalArgumentException> {
            Account.addressFromUncompressedPublicKey(ByteArray(65))
        }
    }

    @Test
    fun `BoundWitnessValidator validateSignature works with recovered address`() {
        val errors = BoundWitnessValidator.validateSignature(
            hexStringToByteArray(testVectorHash),
            testVectorAddress,
            testVectorSignature,
        )
        assertTrue(errors.isEmpty(), "Expected no errors, got: $errors")
    }

    @Test
    fun `BoundWitnessValidator validateSignature flags address mismatch`() {
        val errors = BoundWitnessValidator.validateSignature(
            hexStringToByteArray(testVectorHash),
            "1111111111111111111111111111111111111111",
            testVectorSignature,
        )
        assertEquals(1, errors.size)
        assertTrue(errors.first().message!!.contains("signature address mismatch"))
    }
}
