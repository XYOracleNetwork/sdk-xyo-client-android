package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAccount
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.datastore.PrefsRepository
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertInstanceOf

class XyoPanelTest {
    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)

    lateinit var appContext: Context

    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"
    private val apiDomainLocal = "${TestConstants.nodeUrlLocal}/Archivist"

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun testCreatePanel(nodeUrl: String) {
        val witness = XyoWitness<XyoPayload>(XyoAccount())
        val panel = XyoPanel(appContext, arrayListOf(Pair(nodeUrl, XyoAccount())), listOf(witness))
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
            val witnessAccount = XyoAccount(XyoSerializable.hexToBytes("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"))
            val witness2Account = XyoAccount(XyoSerializable.hexToBytes("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592"))
            val witness = XyoWitness(witnessAccount, fun(_: Context, previousHash: String?): XyoPayload {
                return XyoPayload("network.xyo.basic", previousHash)
            })
            val panel = XyoPanel(appContext, arrayListOf(Pair(nodeUrl, XyoAccount())), listOf(witness, XyoSystemInfoWitness(witness2Account)))
            val result = panel.reportAsyncQuery()
            if (result.apiResults === null) throw NullPointerException("apiResults should not be null")
            result.apiResults?.forEach { assertEquals(it.errors, null) }
        }
    }

    @Test
    fun testPanelReportBeta() {
        testPanelReport(apiDomainBeta)
    }

    /*
    @Test
    fun testPanelReportLocal() {
        testPanelReport(apiDomainLocal)
    }
    */

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSimplePanelReport() {
        runBlocking {
            val panel = XyoPanel(appContext, fun(_context:Context, previousHash: String?): XyoEventPayload {
                return XyoEventPayload("test_event", previousHash)
            })
            val result = panel.reportAsyncQuery()
            if (result.apiResults === null) throw NullPointerException("apiResults should not be null")
            result.apiResults?.forEach { assertEquals(it.errors, null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = XyoPanel(appContext, arrayListOf(Pair(apiDomainBeta, XyoAccount())), listOf(XyoSystemInfoWitness()))
            val result = panel.reportAsyncQuery()
            if (result.apiResults === null) throw Error()
            result.apiResults?.forEach { assertEquals(it.errors, null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMissingNodes() {
        runBlocking {
            val panel = XyoPanel(appContext, arrayListOf(), listOf(XyoSystemInfoWitness()))
            val results = panel.reportAsyncQuery()
            assertInstanceOf<XyoBoundWitnessJson>(results.bw)
            assertInstanceOf<XyoSystemInfoPayload>(results.payloads?.first())
        }
    }

    @Test
    fun testAccountPersistence() {
        runBlocking {
            val prefsRepository = PrefsRepository(appContext)
            prefsRepository.clearSavedAccountKey()

            val panel = XyoPanel(appContext, arrayListOf(Pair(apiDomainBeta, null)), listOf(XyoSystemInfoWitness()))
            panel.resolveNodes()
            val generatedAddress = panel.defaultAccount?.address?.hex
            assertNotEquals(generatedAddress, null)

            val panel2 = XyoPanel(appContext, arrayListOf(Pair(apiDomainBeta, null)), listOf(XyoSystemInfoWitness()))
            panel2.resolveNodes()
            val secondGeneratedAddress = panel2.defaultAccount?.address?.hex
            assertEquals(generatedAddress, secondGeneratedAddress)
        }
    }
}