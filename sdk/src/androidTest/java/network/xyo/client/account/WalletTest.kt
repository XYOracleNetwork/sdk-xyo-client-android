package network.xyo.client.account

import android.util.Log
import org.junit.Test

class WalletTest {
    val words = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
    val privateKeyVectors = arrayOf("m/44'/0'/0'/0/0", "e284129cc0922579a535bbf4d1a3b25773090d28c909bc0fed73b5e0222cc372")

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testWallet()  {
        val wallet = Wallet.fromMnemonic(words, privateKeyVectors[0])
        Log.i("privateKey", wallet.privateKey.toHexString())
        assert(wallet.privateKey.toHexString() == privateKeyVectors[1])
    }
}