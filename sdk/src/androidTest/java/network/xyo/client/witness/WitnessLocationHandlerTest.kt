package network.xyo.client.witness

import network.xyo.client.witness.location.info.WitnessLocationHandler
import android.Manifest
import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.lib.TestConstants
import network.xyo.client.account.Account
import network.xyo.client.boundwitness.XyoBoundWitnessBodyJson
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import network.xyo.client.witness.types.WitnessResult
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.location.info.LocationActivity
import network.xyo.client.witness.location.info.XyoLocationPayload
import network.xyo.client.witness.location.info.XyoLocationPayloadRaw
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertInstanceOf

class WitnessLocationHandlerTest {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule
    val activityRule = ActivityScenarioRule(LocationActivity::class.java)

    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun useAccount() {
        Account.previousHashStore = PreviousHashStorePrefsRepository.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"
    private val apiDomainLocal = "${TestConstants.nodeUrlLocal}/Archivist"

    @Test
    fun testObserve()  {
        runBlocking {
            var firstBw: XyoBoundWitnessJson? = null
            val result1 = WitnessLocationHandler().witness(appContext.applicationContext, arrayListOf(Pair(apiDomainBeta, null)))
            when (result1) {
                is WitnessResult.Success<Triple<XyoBoundWitnessJson?, XyoPayload?, XyoPayload?>> -> {
                    firstBw = result1.data.first
                    assertInstanceOf<XyoBoundWitnessBodyJson>(firstBw)
                    assertInstanceOf<XyoLocationPayload>(result1.data.second)
                    assertInstanceOf<XyoLocationPayloadRaw>(result1.data.third)
                }
                is WitnessResult.Error -> {
                    assert(result1.exception.size > 0)
                }
            }

            var secondBw: XyoBoundWitnessJson? = null
            val result2 = WitnessLocationHandler().witness(appContext.applicationContext, arrayListOf(Pair(apiDomainBeta, null)))
            when (result2) {
                is WitnessResult.Success<Triple<XyoBoundWitnessJson?, XyoPayload?, XyoPayload?>> -> {
                    secondBw = result2.data.first
                    assertInstanceOf<XyoBoundWitnessJson>(secondBw)
                    assertInstanceOf<XyoLocationPayload>(result2.data.second)
                    assertInstanceOf<XyoLocationPayloadRaw>(result2.data.third)

                }
                is WitnessResult.Error -> {
                    assert(result2.exception.size > 0)
                }
            }

            val firstBwHash = firstBw!!.dataHash()
            assert(secondBw!!.previous_hashes.size == 1)
            assert(secondBw.previous_hashes.first() == firstBwHash)
        }
    }
}