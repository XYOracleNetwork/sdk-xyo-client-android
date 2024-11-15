package network.xyo.client.settings

import android.content.Context
import network.xyo.client.account.model.AccountInstance

interface SettingsInterface {
    val accountPreferences: AccountPreferences
    suspend fun getAccount(context: Context): AccountInstance?
}

interface AccountPreferences {
    val fileName: String?
    val storagePath: String?
}