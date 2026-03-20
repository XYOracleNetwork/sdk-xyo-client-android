package network.xyo.client.android.witness.location.info

import android.content.Context
import android.util.Log
import network.xyo.client.android.witness.DeferredObserver
import network.xyo.client.android.witness.XyoWitness
import network.xyo.client.account.Account
import network.xyo.client.payload.Payload

class DeferredLocationObserver : DeferredObserver<Payload>() {
    override suspend fun deferredDetect(
        context: Context,
    ): List<Payload>? {
        try {
            val payloads = XyoLocationPayloads.detect(context)
            // only return the payloads that were found
            val foundPayloads = payloads.takeIf { it != null }
            return if (foundPayloads != null) {
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

class XyoLocationWitness(address: network.xyo.client.account.model.Account = Account.random()) : XyoWitness<Payload>(
    DeferredLocationObserver(),
    address
)
