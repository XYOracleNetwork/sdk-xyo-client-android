package network.xyo.client.settings

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.payload.XyoEventPayload
import network.xyo.client.witness.XyoPanel
import network.xyo.client.account.model.AccountInstance
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertInstanceOf

class XyoSdkTest {

    private lateinit var appContext: Context

    @Before
    fun resetSingleton() {
        XyoSdk.resetInstance()
    }

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testDefaultSettings() {
        runBlocking {
            val instance = XyoSdk.getInstance(appContext)
            assertEquals(instance.settings.accountPreferences.fileName, DefaultXyoSdkSettings().accountPreferences.fileName)
            assertEquals(instance.settings.accountPreferences.storagePath, DefaultXyoSdkSettings().accountPreferences.storagePath)
        }
    }

    @Test
    fun testCustomSettings() {
        runBlocking {
            class UpdatedAccountPreferences : AccountPreferences {
                override val fileName = "network-xyo-sdk-prefs-1"
                override val storagePath = "__xyo-client-sdk-1__"
            }
            class UpdatedSettings: DefaultXyoSdkSettings() {
                override val accountPreferences = UpdatedAccountPreferences()
            }
            val updatedSettings = UpdatedSettings()
            val instance = XyoSdk.getInstance(appContext, updatedSettings)
            assertEquals(instance.settings.accountPreferences.fileName, updatedSettings.accountPreferences.fileName)
            assertEquals(instance.settings.accountPreferences.storagePath, updatedSettings.accountPreferences.storagePath)
        }
    }

    @Test
    fun testGetAccount() {
        runBlocking {
            val instance = XyoSdk.getInstance(appContext)
            val account = instance.getAccount()
            assertInstanceOf<AccountInstance>(account)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testValidChainOnInit() {
        runBlocking {
            val instance = XyoSdk.getInstance(appContext)

            val testAccount = instance.getAccount()
            val panel = XyoPanel(appContext, testAccount, fun(_:Context): List<XyoEventPayload> {
                return listOf(XyoEventPayload("test_event"))
            })
            val result = panel.reportAsyncQuery()
            val bw = result.bw

            val result2 = panel.reportAsyncQuery()
            assert(result2.bw.previous_hashes.contains(bw.hash()))
        }
    }
}