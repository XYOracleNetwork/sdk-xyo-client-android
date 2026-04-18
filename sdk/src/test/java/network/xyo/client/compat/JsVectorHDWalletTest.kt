package network.xyo.client.compat

import network.xyo.client.account.Wallet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Verifies Kotlin BIP-44 HD derivation matches the JS SDK's for multiple
 * mnemonics and paths. The first vector (`m/44'/60'/0'/0/0` of the main
 * mnemonic) is the same fixture that already exists in the instrumentation
 * WalletTestVectors — this test extends coverage to change paths, multiple
 * account indices, and the `abandon abandon ... about` BIP-39 test mnemonic.
 */
@OptIn(ExperimentalStdlibApi::class)
class JsVectorHDWalletTest {

    @TestFactory
    fun `Kotlin Wallet matches JS HD derivation`(): List<DynamicTest> {
        val tests = mutableListOf<DynamicTest>()
        val hdWallets = JsCompatVectors.hdWallets
        for (i in 0 until hdWallets.length()) {
            val group = hdWallets.getJSONObject(i)
            val mnemonic = group.getString("mnemonic")
            val derivations = group.getJSONArray("derivations")
            for (j in 0 until derivations.length()) {
                val d = derivations.getJSONObject(j)
                val path = d.getString("path")
                val expectedPriv = d.getString("private_key")
                val expectedAddress = d.getString("address")
                val expectedPubUncompressed = d.getString("public_key_uncompressed")

                tests += DynamicTest.dynamicTest("derive $path from \"${mnemonic.take(20)}...\"") {
                    val wallet = Wallet.fromMnemonic(mnemonic, path)
                    assertEquals(expectedPriv, wallet.privateKey.toHexString())
                    assertEquals(expectedPubUncompressed, wallet.publicKeyUncompressed.toHexString())
                    assertEquals(expectedAddress, wallet.address.toHexString())
                }
            }
        }
        return tests
    }
}
