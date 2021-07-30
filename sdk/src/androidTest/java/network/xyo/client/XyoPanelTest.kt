package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAddress
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoPanelTest {
    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)

    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testCreatePanel() {
        val apiDomain = "https://beta.archivist.xyo.network"
        val archive = "test"
        val address = XyoAddress()
        val witness = XyoWitness<XyoPayload>(address)
        val panel = XyoPanel(appContext, archive, apiDomain, listOf(witness))
        assertNotNull(address)
        assertNotNull(panel)
    }

    @Test
    fun testPanelReport() {
        runBlocking {
            val apiDomain = "https://beta.archivist.xyo.network"
            val archive = "test"
            val witness = XyoWitness(fun(context: Context, previousHash: String?): XyoPayload {
                return XyoPayload("network.xyo.basic", previousHash)
            })
            val panel = XyoPanel(appContext, archive, apiDomain, listOf(witness, XyoSystemInfoWitness()))
            val result = panel.report()
            result.apiResults.forEach {
                assertEquals(it.errors, null)
            }
        }
    }

    @Test
    fun testSimplePanelReport() {
        runBlocking {
            val panel = XyoPanel(appContext, fun(_context:Context, previousHash: String?): XyoEventPayload {
                return XyoEventPayload("test_event", previousHash)
            })
            val result = panel.report()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }

    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = XyoPanel(appContext, null, null, listOf(XyoSystemInfoWitness()))
            val result = panel.report()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }
}