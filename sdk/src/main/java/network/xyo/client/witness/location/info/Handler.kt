package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import network.xyo.client.witness.types.WitnessHandlerInterface
import network.xyo.client.witness.types.WitnessResult
import network.xyo.client.XyoPanel
import network.xyo.client.account.model.AccountInstance
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
            var bw: XyoPayload? = null
            val errors: MutableList<Error> = mutableListOf()
            panel.let {
                it.reportAsyncQuery().apiResults?.forEach { action ->
                    if (action.response !== null) {
                        val payloads = action.response!!.payloads
                        if (payloads?.get(0)?.schema.equals("network.xyo.boundwitness")) {
                            bw = payloads?.get(0)
                        }
                        if (payloads?.get(1)?.schema.equals("network.xyo.location.android")) {
                            locationPayload = payloads?.get(1)
                        }
                    }
                    if (action.errors !== null) {
                        action.errors.forEach { error ->
                            Log.e("xyoSampleApp", error.message ?: error.toString())
                            errors.add(error)
                        }
                    }
                }
            }
            if (errors.size > 0) return@withContext WitnessResult.Error(errors)
            return@withContext WitnessResult.Success(listOf(bw, locationPayload))
        }
    }
}