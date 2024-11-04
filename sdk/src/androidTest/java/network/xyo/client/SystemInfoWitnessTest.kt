package network.xyo.client

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.witness.system.info.XyoSystemInfoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Test
import org.junit.jupiter.api.assertInstanceOf

class SystemInfoWitnessTest {
    @Test
    fun testObserve()  {
        // Get the application context
        val context = ApplicationProvider.getApplicationContext<Context>()

        val witness = XyoSystemInfoWitness()
        val response = witness.observe(context)
        val bw = response?.first
        val payloads = response?.second
        val systemInfoPayload = payloads?.get(0)
        val errors = response?.third


        assertInstanceOf<XyoBoundWitnessJson>(bw)
        assertInstanceOf<List<Exception>>(errors)
        assertInstanceOf<List<XyoSystemInfoPayload>>(payloads)
        assert(systemInfoPayload?.os != null)
        assert(systemInfoPayload?.device != null)

        // Cannot test network reliably in local jvm tests
        // assert(payload.network != null)
    }
}