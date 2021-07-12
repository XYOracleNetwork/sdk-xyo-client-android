package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule

import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class XyoPanelTest {

    @Rule @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET)


    lateinit var appContext: Context

    @Before
    fun useAppContext() {
        // Context of the app under test.
        this.appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun createClient() {

    }
}