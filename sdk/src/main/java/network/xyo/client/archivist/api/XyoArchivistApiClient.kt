package network.xyo.client.archivist.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

import network.xyo.client.XyoSerializable
import network.xyo.client.payload.XyoPayload

@Deprecated("Use NodeClient instead")
class PostBoundWitnessesResult (
    val count: Int,
    val errors: ArrayList<Error>? = null
    ): XyoSerializable()

@Deprecated("Use NodeClient instead")
open class XyoArchivistApiClient(private val config: XyoArchivistApiConfig) {

    private val okHttp = OkHttpClient()

    val authenticated: Boolean
        get() {
            return this.token != null
        }

    var token: String?
        get() {
            return this.config.token
        }
        set(value) {
            this.config.token = value
        }

    @ExperimentalCoroutinesApi
    private suspend fun postBoundWitnessesAsync (
        entries: List<XyoBoundWitnessJson>
    ): PostBoundWitnessesResult {
        val bodyString = XyoSerializable.toJson(entries)
        val apiDomain = config.apiDomain
        val archive = config.archive

        val request = Request.Builder()
            .url("$apiDomain/archive/$archive/block")
            .post(bodyString.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return withContext(Dispatchers.IO) {
            return@withContext suspendCancellableCoroutine { continuation ->
                try {
                    okHttp.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            continuation.resume(PostBoundWitnessesResult(
                                0,
                                arrayListOf(Error(response.message))
                            ), null)
                        } else {
                            continuation.resume(PostBoundWitnessesResult(1), null)
                        }
                    }
                } catch (ex: IOException) {
                    Log.e("xyoClient", ex.message ?: ex.toString())
                    continuation.resume(PostBoundWitnessesResult(0, arrayListOf(Error(ex.message))), null)
                }
            }
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun postBoundWitnessAsync(
        entry: XyoBoundWitnessJson
    ): PostBoundWitnessesResult {
        return postBoundWitnessesAsync(listOf(entry))
    }

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        fun get(config: XyoArchivistApiConfig): XyoArchivistApiClient {
            return XyoArchivistApiClient(config)
        }
    }
}