package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
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
            val generatedAddress = panel.defaultAccount?.address?.hex
            assertNotEquals(generatedAddress, null)

            val panel2 = XyoPanel(
                appContext, arrayListOf(Pair(apiDomainBeta, null)), listOf(
                    XyoSystemInfoWitness()
                )
            )
            panel2.resolveNodes()
            val secondGeneratedAddress = panel2.defaultAccount?.address?.hex
            assertEquals(generatedAddress, secondGeneratedAddress)
        }
    }

    @Test
    fun testClearingExistingAccount() {
        runBlocking {
            val instance = XyoAccountPrefsRepository.getInstance(appContext)
            val originalAddress = instance.getAccount().private.hex

            instance.clearSavedAccountKey()

            val refreshedAddress = instance.getAccount().private.hex

            assert(originalAddress !== refreshedAddress)
        }
    }

    @Test
    fun testUpdatingAccountPreferences() {
        runBlocking {
            val instance = XyoAccountPrefsRepository.getInstance(appContext)
            val originalAddress = instance.getAccount().private.hex

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

            val refreshedAddress = refreshedInstance.getAccount().private.hex

            assert(originalAddress !== refreshedAddress)
        }
    }
}