package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAddress
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoPanelTest {
    @Rule
    @JvmField
    val grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)

    lateinit var appContext: Context

    val apiDomainBeta = "https://beta.api.archivist.xyo.network"
    val apiDomainLocal = "http://10.0.2.2:80"
    val archive = "temp"

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun testCreatePanel(apiDomain: String) {
        val address = XyoAddress()
        val witness = XyoWitness<XyoPayload>(address)
        val panel = XyoPanel(appContext, archive, apiDomain, listOf(witness))
        assertNotNull(address)
        assertNotNull(panel)
    }

    @Test
    fun testCreatePanelBeta() {
        testCreatePanel(apiDomainBeta)
    }

    @Test
    fun testCreatePanelLocal() {
        testCreatePanel(apiDomainLocal)
    }

    fun testPanelReport(apiDomain: String) {
        runBlocking {
            val apiDomain = apiDomain
            val archive = archive
            val witness = XyoWitness(fun(context: Context, previousHash: String?): XyoPayload {
                return XyoPayload("network.xyo.basic", previousHash)
            })
            val panel = XyoPanel(appContext, archive, apiDomain, listOf(witness, XyoSystemInfoWitness()))
            val result = panel.reportAsync()
            result.apiResults.forEach {
                assertEquals(it.errors, null)
            }
        }
    }

    @Test
    fun testPanelReportBeta() {
        testPanelReport(apiDomainBeta)
    }

    @Test
    fun testPanelReportLocal() {
        testPanelReport(apiDomainLocal)
    }

    @Test
    fun testSimplePanelReport() {
        runBlocking {
            val panel = XyoPanel(appContext, fun(_context:Context, previousHash: String?): XyoEventPayload {
                return XyoEventPayload("test_event", previousHash)
            })
            val result = panel.reportAsync()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }

    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = XyoPanel(appContext, null, null, listOf(XyoSystemInfoWitness()))
            val result = panel.reportAsync()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }
}