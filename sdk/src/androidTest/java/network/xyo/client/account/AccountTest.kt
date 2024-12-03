package network.xyo.client.account

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import org.junit.Before
import org.junit.Test

class AccountTest {

    val testVectorPrivateKey = "7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"
    val testVectorPublicKey
    = "ed6f3b86542f45aab88ec48ab1366b462bd993fec83e234054afd8f2311fba774800fdb40c04918463b463a6044b83413a604550bfba8f8911beb65475d6528e"
    val testVectorAddress = "5e7a847447e7fec41011ae7d32d768f86605ba03"
    val testVectorHash = "4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a"
    val testVectorSignature
    = "b61dad551e910e2793b4f9f880125b5799086510ce102fad0222c1b093c60a6b38aa35ef56f97f86537269e8be95832aaa37d3b64d86b67f0cda467ac7cb5b3e"

    @Before
    fun setupAccount() {
        Account.previousHashStore = PreviousHashStorePrefsRepository.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testRandomAccount()  {
        val account = Account.random()
        assert(account.privateKey.count() == 32)
        assert(account.publicKey.count() == 33)
        assert(account.publicKeyUncompressed.count() == 64)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testKnownPrivateKeyAccount()  {
        runBlocking {
            val account = Account.fromPrivateKey(hexStringToByteArray(testVectorPrivateKey))
            assert(account.privateKey.count() == 32)
            assert(account.publicKey.count() == 33)
            assert(account.publicKeyUncompressed.count() == 64)
            assert(account.publicKeyUncompressed.toHexString() == testVectorPublicKey)
            assert(account.address.toHexString() == testVectorAddress)
            val signature = account.sign(hexStringToByteArray(testVectorHash))
            assert(signature.toHexString() == testVectorSignature)
            assert(account.verify(hexStringToByteArray(testVectorHash), signature))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testPreviousHash()  {
        runBlocking {
            val address = hexStringToByteArray(testVectorPrivateKey)
            val account = Account.fromPrivateKey(address)
            account.sign(hexStringToByteArray(testVectorHash))

            val savedAddressInStore = Account.addressFromUncompressedPublicKey(account.publicKeyUncompressed)
            val previousHashInStore = Account.previousHashStore?.getItem(savedAddressInStore)?.toHexString()
            assert(previousHashInStore == testVectorHash)
        }
    }
}