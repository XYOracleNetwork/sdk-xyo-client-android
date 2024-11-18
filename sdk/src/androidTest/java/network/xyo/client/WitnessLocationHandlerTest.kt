package network.xyo.client

import network.xyo.client.witness.location.info.WitnessLocationHandler
import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.witness.types.WitnessResult
import network.xyo.client.account.Account
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.location.info.LocationActivity
import org.junit.Rule
import org.junit.Test

class WitnessLocationHandlerTest {
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule
    val activityRule = ActivityScenarioRule(LocationActivity::class.java)

    private val apiDomainBeta = "${TestConstants.nodeUrlBeta}/Archivist"
    private val apiDomainLocal = "${TestConstants.nodeUrlLocal}/Archivist"

    @Test
    fun testObserve()  {
        runBlocking {
            // Get the application context
            val context = ApplicationProvider.getApplicationContext<Context>()

            when (val result = WitnessLocationHandler().witness(context.applicationContext, arrayListOf(Pair(apiDomainBeta, Account.random())))) {
                is WitnessResult.Success<List<XyoPayload?>> -> {
                    assert(result.data.size == 2)
                    result.data.map { assert(it !== null)}
                }
                is WitnessResult.Error -> {
                    assert(result.exception.size > 0)
                }
            }
        }
    }
}