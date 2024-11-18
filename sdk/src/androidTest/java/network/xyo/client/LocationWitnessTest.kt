package network.xyo.client

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.xyo.client.witness.location.info.LocationActivity
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

    @get:Rule
    val activityRule = ActivityScenarioRule(LocationActivity::class.java)

    @Test
    fun testObserve()  {
        // Get the application context
        val context = ApplicationProvider.getApplicationContext<Context>()

        CoroutineScope(Dispatchers.Main).launch {
            val witness = XyoLocationWitness()
            val payload = witness.observe(context)?.first()

            assertInstanceOf<XyoLocationPayload>(payload)
            assert(payload.schema == "network.xyo.location.android")
            assert((payload.currentLocation) !== null)
            assert(payload.currentLocation?.coords?.latitude !== null)
            assert(payload.currentLocation?.coords?.longitude !== null)
        }
    }
}