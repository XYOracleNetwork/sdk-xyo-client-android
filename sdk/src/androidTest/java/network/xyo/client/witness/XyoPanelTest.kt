package network.xyo.client.witness

import android.Manifest
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.android.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import network.xyo.client.android.witness.XyoPanel
import network.xyo.client.android.witness.XyoWitness
import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.lib.BasicPayload
import network.xyo.client.lib.TestConstants
import network.xyo.client.payload.EventPayload
import network.xyo.client.payload.Payload
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertInstanceOf

class XyoPanelTest {
    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.INTERNET,
    )

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
        val witness = XyoWitness<Payload>(Account.random())
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
            val witness = XyoWitness(witnessAccount, fun(_: Context): List<Payload> {
                return listOf(BasicPayload())
            })
            val witness2 = XyoWitness(witness2Account, fun(_: Context): List<Payload> {
                return listOf(EventPayload("panel_report"))
            })
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(Pair(nodeUrl, Account.random())), listOf(witness, witness2))
            val result = panel.reportAsyncQuery()
            if (result.apiResults == null) throw NullPointerException("apiResults should not be null")
            assert(result.payloads?.size == 2)
            result.apiResults?.forEach {
                assertEquals(it.errors, null)
            }
        }
    }

    @Test
    fun testPanelReportBeta() {
        testPanelReport(apiDomainBeta)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
    @Test
    fun testSimplePanelReport() {
        runBlocking {
            val testAccount = Account.random()
            val panel = XyoPanel(appContext, testAccount, fun(_: Context): List<EventPayload> {
                return listOf(EventPayload("test_event"))
            })
            val result = panel.reportAsyncQuery()
            if (result.apiResults == null) throw NullPointerException("apiResults should not be null")
            result.apiResults?.forEach { assertEquals(it.errors, null) }
            val bw = result.bw

            val result2 = panel.reportAsyncQuery()
            assert(result2.bw.previous_hashes.contains(bw.dataHash().toHexString()))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testReportEvent() {
        runBlocking {
            val witness = XyoWitness(fun(_: Context): List<Payload> {
                return listOf(EventPayload("report_event"))
            })
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(Pair(apiDomainBeta, Account.random())), listOf(witness))
            val result = panel.reportAsyncQuery()
            if (result.apiResults == null) throw Error()
            result.apiResults?.forEach { assertEquals(it.errors, null) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMissingNodes() {
        runBlocking {
            val witness = XyoWitness(fun(_: Context): List<Payload> {
                return listOf(BasicPayload())
            })
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(), listOf(witness))
            val results = panel.reportAsyncQuery()
            assertInstanceOf<BoundWitness>(results.bw)
            assertInstanceOf<BasicPayload>(results.payloads?.first())
        }
    }
}
