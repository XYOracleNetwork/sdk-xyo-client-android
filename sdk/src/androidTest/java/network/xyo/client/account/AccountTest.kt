package network.xyo.client.account

import android.util.Log
import org.junit.Test

class AccountTest {

    val testVectorPrivateKey = "7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"
    val testVectorPublicKey
    = "04ed6f3b86542f45aab88ec48ab1366b462bd993fec83e234054afd8f2311fba774800fdb40c04918463b463a6044b83413a604550bfba8f8911beb65475d6528e"
    val testVectorAddress = "5e7a847447e7fec41011ae7d32d768f86605ba03"
    val testVectorHash = "4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a"
    val testVectorSignature
    = "b61dad551e910e2793b4f9f880125b5799086510ce102fad0222c1b093c60a6b38aa35ef56f97f86537269e8be95832aaa37d3b64d86b67f0cda467ac7cb5b3e"

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRandomAccount()  {
        val account = Account.random()
        Log.i("account.privateKey", account.privateKey.toHexString())
        Log.i("account.privateKey.count()", account.privateKey.count().toString())
        assert(account.privateKey.count() == 32)
        Log.i("account.publicKey", account.publicKey.toHexString())
        Log.i("account.publicKey.count()", account.publicKey.count().toString())
        assert(account.publicKey.count() == 65)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testKnownPrivateKeyAccount()  {
        val account = Account.fromPrivateKey(hexStringToByteArray(testVectorPrivateKey))
        assert(account.privateKey.count() == 32)
        assert(account.publicKey.count() == 65)
        Log.i("publicKey", account.publicKey.toHexString())
        Log.i("publicKeyVector", testVectorPublicKey)
        Log.i("address", account.address.toHexString())
        Log.i("addressVector", testVectorAddress)
        assert(account.address.toHexString() == testVectorAddress)
        val signature = account.sign(hexStringToByteArray(testVectorHash))
        Log.i("signature", signature.toHexString())
        Log.i("signatureVector", testVectorSignature)
        assert(signature.toHexString() == testVectorSignature)
        assert(account.verify(hexStringToByteArray(testVectorHash), signature))
    }
}