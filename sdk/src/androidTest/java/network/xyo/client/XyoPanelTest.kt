package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.node.client.NodeClient
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

    val apiDomainBeta = "https://beta.api.archivist.xyo.network/Archivist"
    val apiDomainLocal = "http://10.0.2.2:8080/Archivist"
    val archive = "temp"

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun testCreatePanel(nodeUrl: String) {
        val witness = XyoWitness<XyoPayload>(XyoAccount())
        val clients = arrayListOf(NodeClient(nodeUrl, XyoAccount()))
        val panel = XyoPanel(appContext, clients, listOf(witness))
        assertNotNull(clients)
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

    fun testPanelReport(nodeUrl: String) {
        runBlocking {
            val clients = arrayListOf(NodeClient(nodeUrl, XyoAccount()))
            val witnessAccount = XyoAccount(XyoSerializable.hexToBytes("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"))
            val witness2Account = XyoAccount(XyoSerializable.hexToBytes("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592"))
            val witness = XyoWitness(witnessAccount, fun(context: Context, previousHash: String?): XyoPayload {
                return XyoPayload("network.xyo.basic", previousHash)
            })
            val panel = XyoPanel(appContext, clients, listOf(witness, XyoSystemInfoWitness(witness2Account)))
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
            val clients = arrayListOf(NodeClient(apiDomainBeta, XyoAccount()))
            val panel = XyoPanel(appContext, clients, fun(_context:Context, previousHash: String?): XyoEventPayload {
                return XyoEventPayload("test_event", null)
            })
            val result = panel.reportAsync()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }

    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = XyoPanel(appContext, apiDomainBeta, XyoAccount(), listOf(XyoSystemInfoWitness()))
            val result = panel.reportAsync()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }
}