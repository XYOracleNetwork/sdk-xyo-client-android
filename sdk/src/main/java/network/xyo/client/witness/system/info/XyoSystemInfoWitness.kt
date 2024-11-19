package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoWitness
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance

@RequiresApi(Build.VERSION_CODES.M)
class XyoSystemInfoWitness(address: AccountInstance = Account.random()) : XyoWitness<XyoSystemInfoPayload>(
    fun (context: Context): List<XyoSystemInfoPayload> {
        return listOf(XyoSystemInfoPayload.detect(context))
    },
    address
)