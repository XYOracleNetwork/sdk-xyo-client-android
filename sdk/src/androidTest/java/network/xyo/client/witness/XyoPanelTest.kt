package network.xyo.client.witness

import android.Manifest
import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.lib.BasicPayload
import network.xyo.client.lib.TestConstants
import network.xyo.client.payload.XyoEventPayload
import network.xyo.client.account.Account
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.location.info.LocationActivity
import network.xyo.client.witness.location.info.XyoLocationWitness
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
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule
    val activityRule = ActivityScenarioRule(LocationActivity::class.java)

    lateinit var appContext: Context

    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"
    private val apiDomainLocal = "${TestConstants.nodeUrlLocal}/Archivist"

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun useAccount() {
        Account.previousHashStore = PreviousHashStorePrefsRepository.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    private fun testCreatePanel(nodeUrl: String) {
        val witness = XyoWitness<XyoPayload>(Account.random())
        val panel = XyoPanel(appContext, Account.random(), arrayListOf(Pair(nodeUrl, Account.random())), listOf(witness))
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
            val witnessAccount = Account.fromPrivateKey("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
            val witness2Account = Account.fromPrivateKey("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592")
            val witness = XyoWitness(witnessAccount, fun(_: Context): List<XyoPayload> {
                return listOf(BasicPayload())
            })
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(Pair(nodeUrl, Account.random())), listOf(witness, XyoSystemInfoWitness(witness2Account), XyoLocationWitness()))
            val result = panel.reportAsyncQuery()
            if (result.apiResults === null) throw NullPointerException("apiResults should not be null")
            assert(result.payloads?.size == 4)
            result.apiResults?.forEach {
                assertEquals(it.errors, null)
            }
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
            val testAccount = Account.random()
            val panel = XyoPanel(appContext, testAccount, fun(_:Context): List<XyoEventPayload> {
                return listOf(XyoEventPayload("test_event"))
            })
            val result = panel.reportAsyncQuery()
            if (result.apiResults === null) throw NullPointerException("apiResults should not be null")
            result.apiResults?.forEach { assertEquals(it.errors, null) }
            val bw = result.bw

            val result2 = panel.reportAsyncQuery()
            assert(result2.bw.previous_hashes.contains(bw._hash))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(Pair(apiDomainBeta, Account.random())), listOf(XyoSystemInfoWitness()))
            val result = panel.reportAsyncQuery()
            if (result.apiResults === null) throw Error()
            result.apiResults?.forEach { assertEquals(it.errors, null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMissingNodes() {
        runBlocking {
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(), listOf(XyoSystemInfoWitness()))
            val results = panel.reportAsyncQuery()
            assertInstanceOf<XyoBoundWitnessJson>(results.bw)
            assertInstanceOf<XyoSystemInfoPayload>(results.payloads?.first())
        }
    }
}