package network.xyo.client.compat

import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.lib.hexStringToByteArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Verifies Kotlin's Account implementation produces the same address, public
 * key, and signatures as the JS SDK for a fixed set of private keys and
 * message hashes. Because secp256k1 signing is RFC6979-deterministic, any
 * divergence here is a real cross-SDK incompatibility.
 */
@OptIn(ExperimentalStdlibApi::class)
class JsVectorAccountTest {

    @TestFactory
    fun `Kotlin Account matches JS vectors`(): List<DynamicTest> {
        val tests = mutableListOf<DynamicTest>()
        val accounts = JsCompatVectors.accounts
        for (i in 0 until accounts.length()) {
            val vector = accounts.getJSONObject(i)
            val privateKey = vector.getString("private_key")
            val expectedAddress = vector.getString("address")
            val expectedPubKey = vector.getString("public_key_uncompressed")

            tests += DynamicTest.dynamicTest("account $privateKey derives address and pubkey") {
                val account = Account.fromPrivateKey(hexStringToByteArray(privateKey))
                assertEquals(expectedPubKey, account.publicKeyUncompressed.toHexString())
                assertEquals(expectedAddress, account.address.toHexString())
            }

            val signatures = vector.getJSONArray("signatures")
            for (j in 0 until signatures.length()) {
                val sig = signatures.getJSONObject(j)
                val messageHash = sig.getString("message_hash")
                val expectedSig = sig.getString("signature")

                tests += DynamicTest.dynamicTest("account $privateKey signs $messageHash") {
                    runBlocking {
                        // Fresh account per signature so previousHash state from
                        // a prior sign doesn't leak into this one.
                        val account = Account.fromPrivateKey(hexStringToByteArray(privateKey))
                        val actual = account.sign(hexStringToByteArray(messageHash)).toHexString()
                        assertEquals(expectedSig, actual)
                    }
                }
            }
        }
        return tests
    }
}
