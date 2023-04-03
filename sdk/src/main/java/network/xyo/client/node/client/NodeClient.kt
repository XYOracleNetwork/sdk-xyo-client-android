package network.xyo.client.node.client

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import network.xyo.client.XyoSerializable
import network.xyo.client.archivist.api.PostBoundWitnessesResult
import network.xyo.client.archivist.api.XyoArchivistApiClient
import network.xyo.client.payload.XyoPayload
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.logging.Logger

class NodeClient(url: String) {
    val url = url
    private val okHttp = OkHttpClient()

    @ExperimentalCoroutinesApi
    suspend fun callAsync(payload: XyoPayload) {
        val bodyString = XyoSerializable.toJson(payload)

        val request = Request.Builder()
            .url("$payload")
            .post(bodyString.toRequestBody(XyoArchivistApiClient.MEDIA_TYPE_JSON))
            .build()

        return withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine { continuation ->
                try {
                    okHttp.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            println(response.body.string())
                        } else {
                            println("Unsuccessful")
                        }
                    }
                } catch (ex: IOException) {
                    Log.e("xyoClient", ex.message ?: ex.toString())
                }
            }
        }
    }
}