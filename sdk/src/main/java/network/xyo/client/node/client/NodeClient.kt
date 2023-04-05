package network.xyo.client.node.client

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import network.xyo.client.XyoSerializable
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAccount
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.boundwitness.QueryBoundWitnessBuilder
import network.xyo.client.boundwitness.QueryBoundWitnessJson
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.payload.XyoPayload
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

import java.util.logging.Logger
import kotlin.coroutines.Continuation

class PostQueryResult (
    val response: String? = null,
    val errors: ArrayList<Error>? = null
): XyoSerializable()

class NodeClient(private val url: String) {
    private val okHttp = OkHttpClient()

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun query(query: XyoPayload, address: XyoAccount, previousHash: String?): PostQueryResult {
        val bodyString = buildQuery(query, address, previousHash)
        val postBody = bodyString.toRequestBody(XyoArchivistApiClient.MEDIA_TYPE_JSON)
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
    private fun buildQuery(query: XyoPayload, address: XyoAccount, previousHash: String?): String {
        val bw = QueryBoundWitnessBuilder()
            .query(query)
            .witness(address)
            .build(previousHash)
        val bwJson = XyoSerializable.toJson((bw))
        val queryJson = XyoSerializable.toJson(arrayListOf(query))
        val builtQuery = arrayListOf<String>(bwJson, queryJson)
        return builtQuery.joinToString(",", "[", "]")
    }
}