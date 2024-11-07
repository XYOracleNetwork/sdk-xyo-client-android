package network.xyo.client.witness.location.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAccount


@RequiresApi(Build.VERSION_CODES.M)
class XyoLocationInfoWitness(address: XyoAccount = XyoAccount()) : XyoWitness<XyoLocationInfoPayload>(
    address,
    fun (context: Context, _: String?): XyoLocationInfoPayload {
        return XyoLocationInfoPayload.detect(context)
    })