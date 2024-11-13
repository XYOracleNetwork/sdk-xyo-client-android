package network.xyo.client.settings

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.address.XyoAccount
import network.xyo.client.datastore.XyoAccountPrefsRepository

open class DefaultXyoSdkSettings: SettingsInterface {
    override val accountPreferences: AccountPreferences = DefaultAccountPreferences()

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun getAccount(context: Context): XyoAccount? {
        val repository = XyoAccountPrefsRepository.getInstance(context)
        return repository.getAccount()
    }
}

open class DefaultAccountPreferences: AccountPreferences {
    override val fileName = "network-xyo-sdk-prefs"
    override val storagePath = "__xyo-client-sdk__"
}