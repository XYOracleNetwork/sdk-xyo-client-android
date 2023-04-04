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
    val count: Int,
    val errors: ArrayList<Error>? = null
): XyoSerializable()

class NodeClient(private val url: String) {
    private val okHttp = OkHttpClient()

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun callAsync(query: XyoPayload): PostQueryResult {
        val address = XyoAccount()
        val bw = QueryBoundWitnessBuilder()
            .query(query)
            .witness(address)
            .build(null)
        val bwJson = XyoSerializable.toJson((bw))
        val queryJson = XyoSerializable.toJson(arrayListOf(query))
        val items = arrayListOf<String>(bwJson, queryJson)
        val bodyString = items.joinToString(",", "[", "]")
        println(bodyString)
        val postBody = bodyString.toRequestBody(XyoArchivistApiClient.MEDIA_TYPE_JSON)
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/node")
            .post(postBody)
            .build()

        return withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine { continuation ->
                try {
                    okHttp.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            continuation.resume(PostQueryResult(
                                0,
                                arrayListOf(Error(response.message))
                            ), null)
                        } else {
                            continuation.resume(PostQueryResult(1), null)
                        }
                    }
                } catch (ex: IOException) {
                    Log.e("xyoClient", ex.message ?: ex.toString())
                    continuation.resume(PostQueryResult(0, arrayListOf(Error(ex.message))), null)
                }
            }
        }
    }
}