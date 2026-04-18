package network.xyo.chain.protocol.compat

import network.xyo.chain.protocol.sdk.amount.XL1Amount
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.math.BigInteger

/**
 * Verifies Kotlin `XL1Amount` produces identical denomination breakdowns to
 * the JS SDK for a set of fixed AttoXL1 values. Both sides perform integer
 * division by `10^places` — divergence here would mean BigInteger vs bigint
 * semantics differ for this operation, which is a meaningful correctness bug.
 */
class JsVectorXL1AmountTest {

    @TestFactory
    fun `Kotlin XL1Amount conversions match JS vectors`(): List<DynamicTest> {
        val root = loadRoot()
        val vectors = root.getJSONArray("xl1_amounts")
        val tests = mutableListOf<DynamicTest>()
        for (i in 0 until vectors.length()) {
            val v = vectors.getJSONObject(i)
            val label = v.getString("label")
            tests += DynamicTest.dynamicTest("xl1 amount [$label]") {
                val attoInput = BigInteger(v.getString("atto"))
                val amount = XL1Amount.fromAtto(attoInput)
                assertEquals(BigInteger(v.getString("atto")), amount.atto.value, "atto")
                assertEquals(BigInteger(v.getString("femto")), amount.femto.value, "femto")
                assertEquals(BigInteger(v.getString("pico")), amount.pico.value, "pico")
                assertEquals(BigInteger(v.getString("nano")), amount.nano.value, "nano")
                assertEquals(BigInteger(v.getString("micro")), amount.micro.value, "micro")
                assertEquals(BigInteger(v.getString("milli")), amount.milli.value, "milli")
                assertEquals(BigInteger(v.getString("xl1")), amount.xl1.value, "xl1")
            }
        }
        return tests
    }

    private fun loadRoot(): JSONObject {
        val stream = JsVectorXL1AmountTest::class.java.classLoader?.getResourceAsStream("jsCompatVectors.json")
            ?: error("Missing classpath resource: jsCompatVectors.json (regenerate via xl1-compat/generate-vectors.mjs)")
        return JSONObject(stream.use { it.readBytes().toString(Charsets.UTF_8) })
    }
}
