package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import network.xyo.client.DeferredObserver
import network.xyo.client.XyoWitness
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance

class DeferredLocationObserver : DeferredObserver<XyoLocationPayload>() {
    override suspend fun deferredDetect(
        context: Context,
        previousHash: String?
    ): XyoLocationPayload? {
        try {
            return XyoLocationPayload.detect(context)
        } catch (e: Exception) {
            Log.e("xyoClient", "Error building location payload: ${e.toString() + e.stackTraceToString()}")
            return null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
class XyoLocationWitness(address: AccountInstance = Account.random()) : XyoWitness<XyoLocationPayload>(
    DeferredLocationObserver(),
    "",
    address
    )