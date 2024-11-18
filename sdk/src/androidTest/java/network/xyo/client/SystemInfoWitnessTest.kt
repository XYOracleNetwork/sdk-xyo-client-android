package network.xyo.client

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.xyo.client.witness.system.info.XyoSystemInfoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Test
import org.junit.jupiter.api.assertInstanceOf

class SystemInfoWitnessTest {
    @Test
    fun testObserve()  {
        // Get the application context
        val context = ApplicationProvider.getApplicationContext<Context>()

        CoroutineScope(Dispatchers.Main).launch {
            val witness = XyoSystemInfoWitness()
            val payload = witness.observe(context)?.first()

            assertInstanceOf<XyoSystemInfoPayload>(payload)
            assert(payload.os != null)
            assert(payload.device != null)
            assert(payload.network != null)
        }
    }
}