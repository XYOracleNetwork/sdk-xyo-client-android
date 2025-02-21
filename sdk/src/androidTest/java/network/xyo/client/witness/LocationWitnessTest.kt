package network.xyo.client.witness

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import network.xyo.client.account.Account
import network.xyo.client.lib.TestConstants
import network.xyo.client.witness.location.info.LocationActivity
import network.xyo.client.witness.location.info.LocationPayload
import network.xyo.client.witness.location.info.LocationPayloadRaw
import network.xyo.client.witness.location.info.XyoLocationWitness
import org.junit.Before
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

    lateinit var appContext: Context

    @Before
    fun useContext() {
        this.appContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testObserve()  {
        CoroutineScope(Dispatchers.Main).launch {
            val witness = XyoLocationWitness()
            val locationPayload = witness.observe(appContext)?.first()

            assertInstanceOf<LocationPayload>(locationPayload)
            assert(locationPayload.schema == LocationPayload.SCHEMA)
            assert(locationPayload.currentLocation !== null)
            assert(locationPayload.currentLocation?.coords?.latitude !== null)
            assert(locationPayload.currentLocation?.coords?.longitude !== null)

            val locationRawPayload = witness.observe(appContext)?.get(1)
            assertInstanceOf<LocationPayloadRaw>(locationRawPayload)
            assert(locationRawPayload.schema == LocationPayloadRaw.SCHEMA)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
    @Test
    fun testInsidePanel() {
        runBlocking {
            val panel = XyoPanel(appContext, Account.random(), arrayListOf(Pair("${TestConstants.nodeUrlBeta}/Archivist", null)), listOf(XyoLocationWitness()))
            val result = panel.reportAsyncQuery()
            result.payloads?.forEach{ payload ->
                val hash = payload.dataHash().toHexString()
                assert(result.bw.payload_hashes.contains(hash))
            }
        }
    }
}