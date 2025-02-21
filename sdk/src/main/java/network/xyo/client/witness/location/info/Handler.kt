package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import network.xyo.client.witness.types.WitnessHandlerInterface
import network.xyo.client.witness.types.WitnessResult
import network.xyo.client.witness.XyoPanel
import network.xyo.client.account.model.Account
import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.payload.Payload
import network.xyo.client.settings.XyoSdk

open class WitnessLocationHandler : WitnessHandlerInterface<Triple<BoundWitness?, Payload?, Payload?>> {
    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun witness(context: Context, nodeUrlsAndAccounts: ArrayList<Pair<String, Account?>>): WitnessResult<Triple<BoundWitness?, Payload?, Payload?>> {
        val account = XyoSdk.getInstance(context.applicationContext).getAccount(context)
        val panel = XyoPanel(context, account, nodeUrlsAndAccounts, listOf(
            XyoLocationWitness(account)
        ))
        return getLocation(panel)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getLocation(panel: XyoPanel): WitnessResult<Triple<BoundWitness?, Payload?, Payload?>> {
        return withContext(Dispatchers.IO) {
            var locationPayload: Payload? = null
            var locationPayloadRaw: Payload? = null
            var bw: BoundWitness? = null
            val errors: MutableList<Error> = mutableListOf()
            panel.let {
                it.reportAsyncQuery().let { result ->
                    val actualPayloads = result.payloads
                    actualPayloads?.forEach { payload ->
                        when (payload) {
                            is LocationPayload -> locationPayload = payload
                            is LocationPayloadRaw -> locationPayloadRaw = payload
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
            return@withContext WitnessResult.Success(Triple(bw, locationPayload, locationPayloadRaw))
        }
    }
}