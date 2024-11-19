package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import network.xyo.client.DeferredObserver
import network.xyo.client.XyoWitness
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.XyoPayload

class DeferredLocationObserver : DeferredObserver<XyoPayload>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deferredDetect(
        context: Context,
    ): List<XyoPayload>? {
        try {
            val payloads = XyoLocationPayloads.detect(context)
            // only return the payloads that were found
            val foundPayloads = payloads.takeIf { it !== null }
            return if (foundPayloads !== null) {
                listOf(foundPayloads.first, foundPayloads.second)
            } else {
                return null
            }
        } catch (e: Exception) {
            Log.e("xyoClient", "Error building location payload: ${e.toString() + e.stackTraceToString()}")
            return null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
class XyoLocationWitness(address: AccountInstance = Account.random()) : XyoWitness<XyoPayload>(
    DeferredLocationObserver(),
    address
)