package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoWitness
import network.xyo.client.address.Account

@RequiresApi(Build.VERSION_CODES.M)
class XyoSystemInfoWitness(address: Account = Account()) : XyoWitness<XyoSystemInfoPayload>(
    address,
    fun (context: Context, _: String?): XyoSystemInfoPayload {
        return XyoSystemInfoPayload.detect(context)
    })