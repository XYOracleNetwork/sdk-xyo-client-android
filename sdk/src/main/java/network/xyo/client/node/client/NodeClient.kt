package network.xyo.client.node.client

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.boundwitness.QueryBoundWitnessBuilder
import network.xyo.client.boundwitness.QueryBoundWitness
import network.xyo.client.payload.Payload
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.M)
class NodeClient(private val url: String, private val accountToUse: AccountInstance?, private val context: Context) {
    private val _internalAccount = Account.random()
    private val okHttp = OkHttpClient()

    private val account: AccountInstance
        get() {
            if (this.accountToUse === null) {
                println("WARNING: Anonymous Queries not allowed, but running anyway.")
                return this._internalAccount
            }
            return this.accountToUse
        }


    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun query(query: Payload, payloads: List<Payload>?): PostQueryResult {
        val bodyString = buildQuery(query, payloads)
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
                            continuation.resume(
                                PostQueryResult(
                                    null,
                                    arrayListOf(Error(response.message))
                                )
                            ) { cause, _, _ -> null?.let { it(cause) } }
                        } else {
                            continuation.resume(
                                PostQueryResult(
                                    QueryResponseWrapper.parse(response.body!!.string()),
                                    null
                                )
                            ) { cause, _, _ -> null?.let { it(cause) } }
                        }
                    }
                } catch (ex: IOException) {
                    Log.e("xyoClient", ex.message ?: ex.toString())
                    continuation.resume(
                        PostQueryResult(
                            null,
                            arrayListOf(Error(ex.message))
                        )
                    ) { cause, _, _ -> null?.let { it(cause) } }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun buildQuery(query: Payload, payloads: List<Payload>?): String {
        val builtQuery = queryBuilder(query, payloads)
        val queryPayloads = buildQueryPayloads(query, payloads)
        val queryPayloadsJsonArray = queryPayloadsJsonArray(queryPayloads)
        val builtQueryTuple = arrayListOf(JsonSerializable.toJson((builtQuery)), queryPayloadsJsonArray.toString())
        return builtQueryTuple.joinToString(",", "[", "]")
    }

    private suspend fun queryBuilder(query: Payload, payloads: List<Payload>?): QueryBoundWitness {
        return QueryBoundWitnessBuilder().let {
            payloads?.let { payload ->
                it.payloads(payload)
            }
            it.signer(this.account).query(query).build()

        }
    }

    // combine payloads and query
    private fun buildQueryPayloads(query: Payload, payloads: List<Payload>?): List<Payload>{
        return arrayListOf<Payload>().let { queryPayloads ->
            payloads?.let { payloads ->
                payloads.forEach { payload ->
                    queryPayloads.add(payload)
                }
            }
            queryPayloads.add(query)
            queryPayloads
        }
    }

    // stringify combined payloads
    private fun queryPayloadsJsonArray(payloads: List<Payload>): JSONArray {
        return JSONArray().apply {
            payloads.forEach { payload ->
                val serializedPayload = JsonSerializable.toJson(payload)
                val obj = JSONObject(serializedPayload)
                this.put(obj)
            }
        }
    }

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}