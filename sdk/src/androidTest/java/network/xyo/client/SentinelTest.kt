package network.xyo.client

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.Account
import network.xyo.client.module.AdhocWitness
import network.xyo.client.module.AdhocWitnessConfig
import network.xyo.client.module.Archivist
import network.xyo.client.module.ArchivistConfig
import network.xyo.client.module.ArchivistParams
import network.xyo.client.module.ModuleConfig
import network.xyo.client.module.ModuleParams
import network.xyo.client.module.Node
import network.xyo.client.module.WitnessParams
import network.xyo.payload.JSONPayload
import network.xyo.sentinel.Sentinel
import network.xyo.sentinel.SentinelConfig
import network.xyo.sentinel.SentinelParams
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SentinelTest {
    suspend fun testCreateSentinel() {
        val node = Node(ModuleParams(Account(), ModuleConfig()))
        val payloads = setOf(JSONPayload("network.xyo.test"))
        val witness = AdhocWitness(WitnessParams(Account(), AdhocWitnessConfig(payloads)))
        val archivist = Archivist(ArchivistParams(Account(), ArchivistConfig()))
        val sentinel = Sentinel(SentinelParams(Account(), SentinelConfig(setOf(witness.address), setOf(archivist.address))))
        node.register(witness)
        node.register(archivist)
        node.register(sentinel)
        witness.start()
        archivist.start()
        sentinel.start()
        node.start()
        node.attach(witness.address)
        node.attach(archivist.address)
        node.attach(sentinel.address)
        sentinel.report()
        assertEquals(2, archivist.all().size)
    }

    @Test
    fun testCreateSentinelBeta() {
        runBlocking {
            testCreateSentinel()
        }
    }

    @Test
    fun testCreateSentinelLocal() {
        runBlocking {
            testCreateSentinel()
        }
    }

    /*fun testSentinelReport(nodeUrl: String) {
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
    fun testSentinelReportBeta() {
        testSentinelReport(apiDomainBeta)
    }

    @Test
    fun testSentinelReportLocal() {
        testSentinelReport(apiDomainLocal)
    }

    */

    /*

    @Test
    fun testSimpleSentinelReport() {
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