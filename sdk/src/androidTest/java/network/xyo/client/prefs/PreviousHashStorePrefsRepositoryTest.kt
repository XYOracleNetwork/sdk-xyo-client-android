package network.xyo.client.prefs

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import network.xyo.client.lib.TestConstants
import network.xyo.client.account.Account
import network.xyo.client.lib.hexStringToByteArray
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class PreviousHashStorePrefsRepositoryTest {
    private lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testPreviousHashStorePersistence() {
        runBlocking {
            val prefsRepository =
                PreviousHashStorePrefsRepository.getInstance(appContext)
            prefsRepository.clearStore()

            val testAccountAddress = Account.random().address
            val testPreviousHash = hexStringToByteArray(TestConstants.debugPayloadHash)
            prefsRepository.setItem(testAccountAddress, testPreviousHash)

            val savedItem = prefsRepository.getItem(testAccountAddress)
            assertNotNull(savedItem)
            assert(savedItem!!.contentEquals(testPreviousHash))

            prefsRepository.removeItem(testAccountAddress)
            val removedItem = prefsRepository.getItem(testAccountAddress)
            assert(removedItem == null)
        }
    }
}