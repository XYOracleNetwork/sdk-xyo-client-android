package network.xyo.app.xyo.sample.application.witness

import android.content.Context
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.address.XyoAccount

interface WitnessHandlerInterface<out T> {
    suspend fun witness(context: Context, nodeUrlsAndAccounts: ArrayList<Pair<String, AccountInstance?>>): WitnessResult<T>
}