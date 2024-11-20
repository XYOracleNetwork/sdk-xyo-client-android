package network.xyo.client.prefs

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import network.xyo.client.TestConstants
import network.xyo.client.witness.XyoPanel
import network.xyo.client.account.Account
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.datastore.accounts.AccountPrefsRepository
import network.xyo.client.settings.AccountPreferences
import network.xyo.client.settings.PreviousHashStorePreferences
import network.xyo.client.settings.SettingsInterface
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

class AccountPrefsRepositoryTest {

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
                AccountPrefsRepository.getInstance(appContext)
            prefsRepository.clearSavedAccountKey()

            val testAccount = Account.random()

            val panel = XyoPanel(
                appContext, testAccount, arrayListOf(Pair(apiDomainBeta, null)), listOf(
                    XyoSystemInfoWitness()
                )
            )
            panel.resolveNodes()
            val generatedAddress = panel.account.address.toHexString()
            assertNotEquals(generatedAddress, null)

            val panel2 = XyoPanel(
                appContext, testAccount, arrayListOf(Pair(apiDomainBeta, null)), listOf(
                    XyoSystemInfoWitness()
                )
            )
            panel2.resolveNodes()
            val secondGeneratedAddress = panel2.account.address.toHexString()
            assertEquals(generatedAddress, secondGeneratedAddress)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testClearingExistingAccount() {
        runBlocking {
            val instance = AccountPrefsRepository.getInstance(appContext)
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
            val instance = AccountPrefsRepository.getInstance(appContext)
            val originalAddress = instance.getAccount().privateKey.toHexString()

            class UpdatedAccountPreferences : AccountPreferences {
                override val fileName = "network-xyo-sdk-prefs-1"
                override val storagePath = "__xyo-client-sdk-1__"
            }

            class UpdatedPreviousHashShorePreferences : PreviousHashStorePreferences {
                override val fileName = "network-xyo-sdk-prefs-2"
                override val storagePath = "__xyo-client-sdk-1__"
            }

            class Settings: SettingsInterface {
                override val accountPreferences = UpdatedAccountPreferences()
                override val previousHashStorePreferences = UpdatedPreviousHashShorePreferences()
            }

            val updatedSettings = Settings()

            val refreshedInstance =
                AccountPrefsRepository.refresh(appContext, updatedSettings)

            // Test that accountPreferences are updated
            assertEquals(
                refreshedInstance.accountPreferences.fileName,
                updatedSettings.accountPreferences.fileName
            )
            assertEquals(
                refreshedInstance.accountPreferences.storagePath,
                updatedSettings.accountPreferences.storagePath
            )

            val refreshedAddress = refreshedInstance.getAccount().privateKey.toHexString()

            assert(originalAddress !== refreshedAddress)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testAccountDeserialization() {
        runBlocking {
            val testAccount = Account.random()
            val instance = AccountPrefsRepository.getInstance(appContext)
            // Clear previously saved accounts
            instance.clearSavedAccountKey()
            // Serialize the test account
            instance.initializeAccount(testAccount)

            // Deserialize the test account
            val firstAccount = instance.getAccount()
            assertEquals(firstAccount.privateKey.toHexString(), testAccount.privateKey.toHexString())

            // Sign with the test account
            val firstBw = XyoBoundWitnessBuilder(appContext).signer(firstAccount).payloads(listOf(
                TestConstants.debugPayload
            )).build()
            val firstAddress = firstBw.addresses.first()

            // Deserialize the test account (Ideally we would refresh the singleton but in tests this seems to cause errors with multiple instances of the prefs DataStore)
            val secondInstance = AccountPrefsRepository.getInstance(appContext)
            val secondAccount = secondInstance.getAccount()

            // Sign with the test account
            val secondBw = XyoBoundWitnessBuilder(appContext).signer(secondAccount).payloads(listOf(
                TestConstants.debugPayload
            )).build()
            val secondAddress = secondBw.addresses.first()

            // check that addresses have not changed and no errors occurred during signing
            assertEquals(firstAddress, secondAddress)
        }
    }
}