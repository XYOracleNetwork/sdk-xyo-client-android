package network.xyo.client.android.settings

import android.content.Context
import network.xyo.client.account.Account
import network.xyo.client.android.datastore.accounts.AccountPrefsRepository
import network.xyo.client.android.datastore.previous_hash_store.PreviousHashStorePrefsRepository
import network.xyo.client.settings.SettingsInterface
import network.xyo.client.settings.DefaultXyoSdkSettings

class XyoSdk private constructor(val settings: SettingsInterface) {
    private var _account: network.xyo.client.account.model.Account? = null

    suspend fun getAccount(context: Context): network.xyo.client.account.model.Account {
        if (INSTANCE != null) {
            val validInstance = INSTANCE!!
            if (validInstance._account != null) {
                return validInstance._account!!
            } else {
                val repository = AccountPrefsRepository.getInstance(context.applicationContext)
                validInstance._account = repository.getAccount()
                return _account!!
            }
        } else {
            throw Exception("Tried to access instance but it was null.  Did you forget to initialize XyoSdk first?")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: XyoSdk? = null

        fun getInstance(context: Context, settings: SettingsInterface = DefaultXyoSdkSettings()): XyoSdk {
            // Initialize the global AccountPrefs DataStore
            AccountPrefsRepository.getInstance(context.applicationContext, settings)
            // Initialize the global Account object with the previous hash store DataStore
            Account.previousHashStore = PreviousHashStorePrefsRepository.getInstance(context.applicationContext)

            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: XyoSdk(settings).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, settings: SettingsInterface = DefaultXyoSdkSettings()): XyoSdk {
            synchronized(this) {
                // Initialize the singleton with the users accountPreferences
                AccountPrefsRepository.getInstance(context, settings)
                INSTANCE = XyoSdk(settings)
            }
            return INSTANCE!!
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}
