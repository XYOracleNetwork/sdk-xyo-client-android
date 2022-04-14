package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAddress

@RequiresApi(Build.VERSION_CODES.M)
class XyoSystemInfoWitness(address: XyoAddress = XyoAddress()) : XyoWitness<XyoSystemInfoPayload>(
    address,
    fun (context: Context, _: String?): XyoSystemInfoPayload {
        return XyoSystemInfoPayload.detect(context)
    })