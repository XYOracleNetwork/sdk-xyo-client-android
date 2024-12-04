package network.xyo.client.account

import android.util.Log
import org.junit.Test

data class TestVector(
    val path: String,
    val address: String,
    val privateKey: String,
    val publicKey: String
)

class WalletTestVectors {
    val phrase = "later puppy sound rebuild rebuild noise ozone amazing hope broccoli crystal grief"
    val testVectors = arrayOf(
        TestVector(
            "m/44'/60'/0'/0/0",
            "e46c258c74c7c1df33d7caa4c2c664dc0843ab3f",
            "96a7705eedbb701a03ee235911253fd3eb80e48a06106c0bf957d42b72bd8efa",
            "03a9f10779cb44e73a1983b8225ce9de96ff63cbc8a2900db102fa55a38a14b206"))

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testWallet()  {
        val index = 0
        val vector = this.testVectors[index]
        val wallet = Wallet.fromMnemonic(phrase, vector.path)
        Log.i("privateKey", wallet.privateKey.toHexString())
        val calcPrivateKey = wallet.privateKey.toHexString()
        val calcPublicKey = wallet.publicKey.toHexString()
        val calcAddress = wallet.address.toHexString()
        assert(calcPrivateKey == vector.privateKey)
        assert(calcPublicKey == vector.publicKey)
        assert(calcAddress == vector.address)
    }
}