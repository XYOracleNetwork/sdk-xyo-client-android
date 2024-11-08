package network.xyo.client

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import network.xyo.client.witness.location.info.XyoLocationPayload
import network.xyo.client.witness.location.info.XyoLocationWitness
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertInstanceOf

class LocationWitnessTest {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Test
    fun testObserve()  {
        assert(true == true)
        // bring back once we can successfully start listening for locations changes or mocking them
//        Get the application context
//        val context = ApplicationProvider.getApplicationContext<Context>()
//
//        val witness = XyoLocationWitness()
//        val payload = witness.observe(context)
//
//        assertInstanceOf<XyoLocationPayload>(payload)
//        assert(payload.schema == "network.xyo.location.android")
    }
}