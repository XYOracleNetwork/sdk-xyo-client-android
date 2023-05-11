package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.Account
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class BoundWitnessTest {

    val apiDomainBeta = "https://beta.api.archivist.xyo.network"
    val apiDomainLocal = "http://10.0.2.2:8080"

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE)


    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /*fun generateQuery(nodeUrl: String): RequestDependencies {
        val account = Account()
        val client = NodeClient(nodeUrl, account)
        val query = XyoPayload("network.xyo.query.module.discover")
        val payloads = mutableListOf<XyoPayload>()
        payloads.add(XyoTestPayload1())
        return RequestDependencies(client, query, payloads)
    }*/

    /*fun testSendQueryBW(nodeUrl: String) {
        runBlocking {
            val(client, query, payloads) = generateQuery(nodeUrl)
            val postResult = client.query(query, payloads, null)
            assertEquals(null, postResult.errors)
        }
    }*/

    @Test
    fun testSendQueryBWSendLocal() {
        //testSendQueryBW(apiDomainLocal)
    }

    @Test
    fun testSendQueryBWSendBeta() {
        //testSendQueryBW(apiDomainBeta)
    }
}