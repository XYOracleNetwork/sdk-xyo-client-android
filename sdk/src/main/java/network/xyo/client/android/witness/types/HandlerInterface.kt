package network.xyo.client.android.witness.types

import android.content.Context
import network.xyo.client.account.model.Account
import network.xyo.client.witness.types.WitnessResult

interface WitnessHandlerInterface<out T> {
    suspend fun witness(context: Context, nodeUrlsAndAccounts: ArrayList<Pair<String, Account?>>): WitnessResult<T>
}
