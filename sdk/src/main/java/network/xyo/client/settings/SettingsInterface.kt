package network.xyo.client.settings

import android.content.Context
import network.xyo.client.address.XyoAccount

interface SettingsInterface {
    val accountPreferences: AccountPreferences
    suspend fun getAccount(context: Context): XyoAccount?
}

interface AccountPreferences {
    val fileName: String?
    val storagePath: String?
}