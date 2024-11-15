package network.xyo.client.node.client

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.XyoSerializable
import network.xyo.client.boundwitness.XyoBoundWitnessBodyJson
import network.xyo.client.payload.XyoPayload
import org.json.JSONArray
import org.json.JSONObject

data class QueryResponse(val data: String) {}

open class QueryResponseWrapper(private val rawResponse: String) {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    var bwHash: String? = null
    var bw: XyoBoundWitnessBodyJson? = null
    /** The bound witness that was first in the list of payloads */
    var panelBoundWitnessBodyJson: XyoBoundWitnessBodyJson? = null
    var panelBoundWitnessHash: String? = null
    var payloads: List<XyoPayload>? = null

    private fun unwrap() {
        val response = JSONObject(rawResponse)
        val data = response.get("data") as JSONArray
        return splitTuple(data)
    }

    private fun splitTuple(tuple: JSONArray) {
        val wrapperBwString = tuple[0].toString()
        val wrapperBw = parseBW(wrapperBwString)
        if (wrapperBw !== null) {
            bwHash = wrapperBw.hash()
            bw = wrapperBw
        }

        val payloadsString = tuple[1].toString()
        val payloadsArray = JSONArray(payloadsString)
        // grab the first payload and see if it is a boundwitness
        try {
            val bwJson = payloadsArray.get(0) as JSONObject
            if (bwJson.get("schema") !== "network.xyo.boundwitness") {
                val panelBw = parseBW(bwJson.toString())
                panelBoundWitnessBodyJson = panelBw
                panelBoundWitnessHash = panelBw?.hash()

            }
        } catch (e: Exception) {
            Log.i("xyoClient", "skipping bw parsing from payloads")
        }
        payloads = parsePayloads(payloadsString)
    }

    protected open fun parseBW(bwString: String): XyoBoundWitnessBodyJson? {
        val bwAdapter = moshi.adapter(XyoBoundWitnessBodyJson::class.java)
        val bw = bwAdapter.fromJson(bwString)
        return bw
    }


    /**
     * Parse payloads
     *
     * Override to parse payloads into their specific types
     *
     * @param payloadsString
     * @return
     */
    open fun parsePayloads(payloadsString: String): List<XyoPayload>? {
        val type = Types.newParameterizedType(List::class.java, XyoPayload::class.java)
        val payloadAdapter = moshi.adapter<List<XyoPayload>>(type)
        return payloadAdapter.fromJson(payloadsString)
    }

    companion object {
        fun parse(rawResponse: String?): QueryResponseWrapper? {
            if (rawResponse != null) {
                val instance = QueryResponseWrapper(rawResponse)
                instance.unwrap()
                return instance
            }
            return null
        }
    }
}