package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.Account
import network.xyo.client.module.AdhocWitness
import network.xyo.client.module.Archivist
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.Node
import network.xyo.client.module.Sentinel
import network.xyo.client.module.SentinelConfig
import network.xyo.client.module.SentinelParams
import network.xyo.client.witness.system.info.SystemInfoWitness
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

    suspend fun testCreateSentinel() {
        val node = Node(ModuleParams(Account(), ModuleConfig()))
        val witness = AdhocWitness(ModuleParams(Account(), ModuleConfig()))
        val archivist = Archivist(ModuleParams(Account(), ModuleConfig()))
        val sentinel = Sentinel(SentinelParams(appContext, Account(), SentinelConfig(setOf(witness.address), setOf(archivist.address))))
        node.register(witness)
        node.register(archivist)
        node.register(sentinel)
        node.attach(witness.address)
        node.attach(archivist.address)
        node.attach(sentinel.address)
        sentinel.report()
        assertEquals(1, archivist.all().size)
    }

    @Test
    fun testCreatePanelBeta() {
        runBlocking {
            testCreateSentinel()
        }
    }

    @Test
    fun testCreatePanelLocal() {
        runBlocking {
            testCreateSentinel()
        }
    }

    /*fun testPanelReport(nodeUrl: String) {
        runBlocking {
            val witnessAccount = Account(XyoSerializable.hexToBytes("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"))
            val witness2Account = Account(XyoSerializable.hexToBytes("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592"))
            val witness = XyoWitness(witnessAccount, fun(context: Context, previousHash: String?): XyoPayload {
                return XyoPayload("network.xyo.basic", previousHash)
            })
            val panel = Sentinel(appContext, arrayListOf(Pair(nodeUrl, Account())), listOf(witness, SystemInfoWitness(witness2Account)))
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

    */

    /*

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


    @Test
    fun testReportEvent() {
        runBlocking {
            val panel = Sentinel(appContext, arrayListOf(Pair(apiDomainBeta, Account())), listOf(SystemInfoWitness()))
            val result = panel.reportAsyncQuery()
            result.apiResults.forEach { assertEquals(it.errors, null) }
        }
    }

    */
}