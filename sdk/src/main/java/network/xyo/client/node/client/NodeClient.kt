package network.xyo.client.node.client

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import network.xyo.client.XyoSerializable
import network.xyo.client.address.XyoAccount
import network.xyo.client.boundwitness.QueryBoundWitnessBuilder
import network.xyo.client.payload.XyoPayload
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class PostQueryResult (
    val response: String? = null,
    val errors: ArrayList<Error>? = null
): XyoSerializable() {
    operator fun component1() = response
    operator fun component2() = errors
}

@RequiresApi(Build.VERSION_CODES.M)
class NodeClient(private val url: String, private val accountToUse: XyoAccount?) {

    private val _internalAccount = XyoAccount()
    private val okHttp = OkHttpClient()

    private val account: XyoAccount
        get() {
            if (this.accountToUse === null) {
                println("WARNING: Anonymous Queries not allowed, but running anyway.")
                return this._internalAccount
            }
            return this.accountToUse
        }


    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun query(query: XyoPayload, payloads: List<XyoPayload>?, previousHash: String?): PostQueryResult {
        val bodyString = buildQuery(query, payloads, previousHash)
        val postBody = bodyString.toRequestBody(MEDIA_TYPE_JSON)
        val request = Request.Builder()
            .url(url)
            .post(postBody)
            .build()

        return withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine { continuation ->
                try {
                    okHttp.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            continuation.resume(PostQueryResult(
                                null,
                                arrayListOf(Error(response.message))
                            ), null)
                        } else {
                            continuation.resume(PostQueryResult(response.body!!.string(), null), null)
                        }
                    }
                } catch (ex: IOException) {
                    Log.e("xyoClient", ex.message ?: ex.toString())
                    continuation.resume(PostQueryResult(null, arrayListOf(Error(ex.message))), null)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun buildQuery(query: XyoPayload, payloads: List<XyoPayload>?, previousHash: String?): String {
        val queryBw = QueryBoundWitnessBuilder().witness(this.account, previousHash)
        payloads?.let {
            queryBw.payloads(it)
        }
        val builtQuery = queryBw.query(query).build(previousHash)

        // combine payloads and query
        val queryPayloads = ArrayList<XyoPayload>()
        payloads?.let {
            payloads.forEach {
                queryPayloads.add(it)
            }
        }
        queryPayloads.add(query)

        // stringify combined payloads
        val queryPayloadsJsonArray = JSONArray().apply {
            queryPayloads.forEach {
                val serializedPayload = XyoSerializable.toJson(it)
                val obj = JSONObject(serializedPayload)
                this.put(obj)
            }
        }
        val builtQueryTuple = arrayListOf(XyoSerializable.toJson((builtQuery)), queryPayloadsJsonArray.toString())
        return builtQueryTuple.joinToString(",", "[", "]")
    }

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}