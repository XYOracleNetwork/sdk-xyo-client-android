package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import network.xyo.client.witness.types.WitnessHandlerInterface
import network.xyo.client.witness.types.WitnessResult
import network.xyo.client.XyoPanel
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.boundwitness.XyoBoundWitnessBodyJson
import network.xyo.client.payload.XyoPayload

open class WitnessLocationHandler : WitnessHandlerInterface<List<XyoPayload?>> {
    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun witness(context: Context, nodeUrlsAndAccounts: ArrayList<Pair<String, AccountInstance?>>): WitnessResult<List<XyoPayload?>> {
        val panel = XyoPanel(context, nodeUrlsAndAccounts, listOf(
            XyoLocationWitness()
        ))
        return getLocation(panel)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getLocation(panel: XyoPanel): WitnessResult<List<XyoPayload?>> {
        return withContext(Dispatchers.IO) {
            var locationPayload: XyoPayload? = null
            var bw: XyoBoundWitnessBodyJson? = null
            val errors: MutableList<Error> = mutableListOf()
            panel.let {
                it.reportAsyncQuery().let { result ->
                    val actualPayloads = result.payloads
                    actualPayloads?.forEach { payload ->
                        if (payload.schema === "network.xyo.location.android") {
                            locationPayload = payload
                        }
                    }

                    // target the first result because we are only looking at a single location witness
                    val apiResult = result.apiResults?.first()
                    if (apiResult?.errors?.size !== null && apiResult.errors.size > 0) {
                        apiResult.errors.forEach { error -> errors.add(error)}
                    }

                    bw = result.bw
                }
            }
            if (errors.size > 0) return@withContext WitnessResult.Error(errors)
            return@withContext WitnessResult.Success(listOf(bw, locationPayload))
        }
    }
}