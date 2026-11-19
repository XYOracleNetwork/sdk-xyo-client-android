package network.xyo.client.android.witness.system.info

import android.content.Context
import network.xyo.client.android.witness.XyoWitness
import network.xyo.client.account.Account

class XyoSystemInfoWitness(address: network.xyo.client.account.model.Account = Account.random()) : XyoWitness<SystemInfoPayload>(
    fun (context: Context): List<SystemInfoPayload> {
        return listOf(SystemInfoPayload.detect(context))
    },
    address
)
