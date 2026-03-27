package network.xyo.chain.protocol.rpc.transport

import kotlinx.coroutines.suspendCancellableCoroutine
import network.xyo.chain.protocol.rpc.schema.rpcMoshi
import network.xyo.chain.protocol.rpc.types.JsonRpcRequest
import network.xyo.chain.protocol.rpc.types.JsonRpcResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HttpRpcTransport(
    private val rpcUrl: String,
    private val client: OkHttpClient = defaultClient(),
) : RpcTransport {

    private val requestAdapter = rpcMoshi.adapter(JsonRpcRequest::class.java)
    private val responseAdapter = rpcMoshi.adapter(JsonRpcResponse::class.java)

    override suspend fun sendRawRequest(method: String, params: List<Any?>): Any? {
        val request = JsonRpcRequest(
            id = UUID.randomUUID().toString(),
            method = method,
            params = params,
        )

        val jsonBody = requestAdapter.toJson(request)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonBody.toRequestBody(mediaType)

        val httpRequest = Request.Builder()
            .url(rpcUrl)
            .post(body)
            .header("Content-Type", "application/json")
            .build()

        val responseBody = suspendCancellableCoroutine { continuation ->
            val call = client.newCall(httpRequest)
            continuation.invokeOnCancellation { call.cancel() }

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(
                        RpcTransportException("RPC call to $method failed: ${e.message}", e)
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string()
                            ?: throw RpcTransportException("Empty response body for $method")
                        continuation.resume(body)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }

        val rpcResponse = responseAdapter.fromJson(responseBody as String)
            ?: throw RpcTransportException("Failed to parse JSON-RPC response for $method")

        if (rpcResponse.isError) {
            val error = rpcResponse.error!!
            throw RpcTransportException(
                "RPC error for $method [${error.code}]: ${error.message}"
            )
        }

        return rpcResponse.result
    }

    companion object {
        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}

class RpcTransportException(message: String, cause: Throwable? = null) : Exception(message, cause)
