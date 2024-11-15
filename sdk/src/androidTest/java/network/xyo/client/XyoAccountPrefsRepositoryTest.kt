package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.datastore.XyoAccountPrefsRepository
import network.xyo.client.settings.AccountPreferences
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

class XyoAccountPrefsRepositoryTest {

    private lateinit var appContext: Context

    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testAccountPersistence() {
        runBlocking {
            val prefsRepository =
                XyoAccountPrefsRepository.getInstance(appContext)
            prefsRepository.clearSavedAccountKey()

            val panel = XyoPanel(
                appContext, arrayListOf(Pair(apiDomainBeta, null)), listOf(
                    XyoSystemInfoWitness()
                )
            )
            panel.resolveNodes()
            val generatedAddress = panel.defaultAccount?.address?.toHexString()
            assertNotEquals(generatedAddress, null)

            val panel2 = XyoPanel(
                appContext, arrayListOf(Pair(apiDomainBeta, null)), listOf(
                    XyoSystemInfoWitness()
                )
            )
            panel2.resolveNodes()
            val secondGeneratedAddress = panel2.defaultAccount?.address?.toHexString()
            assertEquals(generatedAddress, secondGeneratedAddress)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testClearingExistingAccount() {
        runBlocking {
            val instance = XyoAccountPrefsRepository.getInstance(appContext)
            val originalAddress = instance.getAccount().privateKey.toHexString()

            instance.clearSavedAccountKey()

            val refreshedAddress = instance.getAccount().privateKey.toHexString()

            assert(originalAddress !== refreshedAddress)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testUpdatingAccountPreferences() {
        runBlocking {
            val instance = XyoAccountPrefsRepository.getInstance(appContext)
            val originalAddress = instance.getAccount().privateKey.toHexString()

            class UpdatedAccountPreferences : AccountPreferences {
                override val fileName = "network-xyo-sdk-prefs-1"
                override val storagePath = "__xyo-client-sdk-1__"
            }

            val updatedAccountPrefs = UpdatedAccountPreferences()

            val refreshedInstance =
                XyoAccountPrefsRepository.refresh(appContext, updatedAccountPrefs)

            // Test that accountPreferences are updated
            assertEquals(
                refreshedInstance.accountPreferences.fileName,
                updatedAccountPrefs.fileName
            )
            assertEquals(
                refreshedInstance.accountPreferences.storagePath,
                updatedAccountPrefs.storagePath
            )

            val refreshedAddress = refreshedInstance.getAccount().privateKey.toHexString()

            assert(originalAddress !== refreshedAddress)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testAccountDeserialization() {
        runBlocking {
            val testAccount = XyoAccount()
            val instance = XyoAccountPrefsRepository.getInstance(appContext)
            // Clear previously saved accounts
            instance.clearSavedAccountKey()
            // Serialize the test account
            instance.initializeAccount(testAccount)

            // Deserialize the test account
            val firstAccount = instance.getAccount()
            assertEquals(firstAccount.privateKey.toHexString(), testAccount.private.hex)

            // Sign with the test account
            val firstBw = XyoBoundWitnessBuilder().witness(firstAccount, null).payloads(listOf(TestConstants.debugPayload)).build()
            val firstAddress = firstBw.addresses.first()

            // Deserialize the test account (Ideally we would refresh the singleton but in tests this seems to cause errors with multiple instances of the prefs DataStore)
            val secondInstance = XyoAccountPrefsRepository.getInstance(appContext)
            val secondAccount = secondInstance.getAccount()

            // Sign with the test account
            val secondBw = XyoBoundWitnessBuilder().witness(secondAccount, null).payloads(listOf(TestConstants.debugPayload)).build()
            val secondAddress = secondBw.addresses.first()

            // check that addresses have not changed and no errors occurred during signing
            assertEquals(firstAddress, secondAddress)
        }
    }
}