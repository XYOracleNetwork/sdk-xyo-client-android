package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.Account
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class SentinelTest {
    @Rule
    @JvmField
    val grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)

    lateinit var appContext: Context

    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"
    private val apiDomainLocal = "${TestConstants.nodeUrlLocal}/Archivist"

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun testCreatePanel(nodeUrl: String) {
        val witness = XyoWitness<XyoPayload>(Account())
        val panel = Sentinel(appContext, arrayListOf(Pair(nodeUrl, Account())), listOf(witness))
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun testPanelReport(nodeUrl: String) {
        runBlocking {
            val witnessAccount = Account(XyoSerializable.hexToBytes("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"))
            val witness2Account = Account(XyoSerializable.hexToBytes("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592"))
            val witness = XyoWitness(witnessAccount, fun(context: Context, previousHash: String?): XyoPayload {
                return XyoPayload("network.xyo.basic", previousHash)
            })
            val panel = Sentinel(appContext, arrayListOf(Pair(nodeUrl, Account())), listOf(witness, XyoSystemInfoWitness(witness2Account)))
            val result = panel.reportAsyncQuery()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSimplePanelReport() {
        runBlocking {
            val panel = Sentinel(appContext, fun(_context:Context, previousHash: String?): XyoEventPayload {
                return XyoEventPayload("test_event", previousHash)
            })
            val result = panel.reportAsyncQuery()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = Sentinel(appContext, arrayListOf(Pair(apiDomainBeta, Account())), listOf(XyoSystemInfoWitness()))
            val result = panel.reportAsyncQuery()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }
}