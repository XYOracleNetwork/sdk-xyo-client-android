package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.witness.XyoWitness
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance

@RequiresApi(Build.VERSION_CODES.M)
class XyoSystemInfoWitness(address: AccountInstance = Account.random()) : XyoWitness<SystemInfoPayload>(
    fun (context: Context): List<SystemInfoPayload> {
        return listOf(SystemInfoPayload.detect(context))
    },
    address
)