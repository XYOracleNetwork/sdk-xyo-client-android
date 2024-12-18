package network.xyo.client.settings

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.Account
import network.xyo.client.datastore.accounts.AccountPrefsRepository
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository

class XyoSdk private constructor(val settings: SettingsInterface) {
    private var _account: network.xyo.client.account.model.Account? = null

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getAccount(context: Context): network.xyo.client.account.model.Account {
        if (INSTANCE !== null) {
            val validInstance = INSTANCE!!
            if (validInstance._account !== null) {
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