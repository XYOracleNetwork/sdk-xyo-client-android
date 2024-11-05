package network.xyo.client

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
        val payload = witness.observe(context)

        assertInstanceOf<XyoSystemInfoPayload>(payload)
        assert(payload.os != null)
        assert(payload.device != null)

        // Cannot test network reliably in local jvm tests
        // assert(payload.network != null)
    }
}