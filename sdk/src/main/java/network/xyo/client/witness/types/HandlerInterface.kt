package network.xyo.client.witness.types

import android.content.Context
import network.xyo.client.account.model.Account

interface WitnessHandlerInterface<out T> {
    suspend fun witness(context: Context, nodeUrlsAndAccounts: ArrayList<Pair<String, Account?>>): WitnessResult<T>
}