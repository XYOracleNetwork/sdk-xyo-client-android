package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.DeferredObserver
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAccount

class DeferredLocationObserver : DeferredObserver<XyoLocationPayload>() {
    override suspend fun deferredDetect(
        context: Context,
        previousHash: String?
    ): XyoLocationPayload? {
        return XyoLocationPayload.detect(context)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
class XyoLocationWitness(address: XyoAccount = XyoAccount()) : XyoWitness<XyoLocationPayload>(
    DeferredLocationObserver(),
    "",
    address
    )