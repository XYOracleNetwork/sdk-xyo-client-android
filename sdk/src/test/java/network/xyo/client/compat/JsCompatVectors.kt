package network.xyo.client.compat

import org.json.JSONArray
import org.json.JSONObject

/**
 * Loader for the cross-SDK vector file produced by xl1-compat/generate-vectors.mjs.
 *
 * Each JsVector*Test class pulls a slice of this file and asserts the Kotlin
 * implementation produces bit-identical output to the JS SDK. The vector file
 * is a committed, regeneration-reviewed contract — see xl1-compat/README.md.
 */
object JsCompatVectors {

    private const val RESOURCE = "jsCompatVectors.json"

    val root: JSONObject by lazy {
        val stream = JsCompatVectors::class.java.classLoader?.getResourceAsStream(RESOURCE)
            ?: error("Missing classpath resource: $RESOURCE (regenerate via xl1-compat/generate-vectors.mjs)")
        val text = stream.use { it.readBytes().toString(Charsets.UTF_8) }
        JSONObject(text)
    }

    val accounts: JSONArray get() = root.getJSONArray("accounts")
    val hdWallets: JSONArray get() = root.getJSONArray("hd_wallets")
    val payloadHashes: JSONArray get() = root.getJSONArray("payload_hashes")
    val boundWitnesses: JSONArray get() = root.getJSONArray("bound_witnesses")
    val xl1Amounts: JSONArray get() = root.getJSONArray("xl1_amounts")
}
