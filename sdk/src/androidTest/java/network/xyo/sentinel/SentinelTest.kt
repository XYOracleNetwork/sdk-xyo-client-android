package network.xyo.sentinel

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import network.xyo.account.Account
import network.xyo.witness.AdhocWitness
import network.xyo.witness.AdhocWitnessConfig
import network.xyo.archivist.Archivist
import network.xyo.archivist.ArchivistConfig
import network.xyo.archivist.ArchivistParams
import network.xyo.module.ModuleConfig
import network.xyo.module.ModuleParams
import network.xyo.node.Node
import network.xyo.witness.WitnessParams
import network.xyo.payload.JSONPayload
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SentinelTest {

    @Test
    fun simpleReport() {
        runBlocking {
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
    }
}