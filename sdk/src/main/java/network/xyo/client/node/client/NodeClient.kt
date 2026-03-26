package network.xyo.client.node.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.account.Account
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

class NodeClient(private val url: String, private val accountToUse: network.xyo.client.account.model.Account?) {
    private val _internalAccount = Account.random()
    private val okHttp = OkHttpClient()

    private val account: network.xyo.client.account.model.Account
        get() {
            if (this.accountToUse == null) {
                return this._internalAccount
            }
            return this.accountToUse
        }


    @ExperimentalCoroutinesApi
    suspend fun query(query: Payload, payloads: List<Payload>?): PostQueryResult {
        val bodyString = buildQuery(query, payloads)
        val postBody = bodyString.toRequestBody(MEDIA_TYPE_JSON)
        val request = Request.Builder()
            .url(url)
            .post(postBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                okHttp.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        PostQueryResult(null, arrayListOf(Error(response.message)))
                    } else {
                        PostQueryResult(
                            QueryResponseWrapper.parse(response.body!!.string()),
                            null
                        )
                    }
                }
            } catch (ex: IOException) {
                PostQueryResult(null, arrayListOf(Error(ex.message)))
            }
        }
    }

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
            val (qbw, _) = it.signer(this.account).query(query).build()
            qbw
        }
    }

    // combine payloads and query
    private fun buildQueryPayloads(query: Payload, payloads: List<Payload>?): List<Payload>{
        return arrayListOf<Payload>().let { queryPayloads ->
            payloads?.let { payloads ->
                for (payload in payloads) {
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
            for (payload in payloads) {
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