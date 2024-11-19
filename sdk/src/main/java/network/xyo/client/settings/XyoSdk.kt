package network.xyo.client.settings

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.datastore.accounts.AccountPrefsRepository
import network.xyo.client.datastore.previous_hash_store.PreviousHashStorePrefsRepository

class XyoSdk private constructor(context: Context, val settings: SettingsInterface) {
    private val appContext = context.applicationContext
    private var _account: AccountInstance? = null

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getAccount(): AccountInstance {
        if (INSTANCE !== null) {
            val validInstance = INSTANCE!!
            if (validInstance._account !== null) {
                return validInstance._account!!
            } else {
                val repository = AccountPrefsRepository.getInstance(validInstance.appContext)
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
                INSTANCE ?: XyoSdk(context.applicationContext, settings).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, settings: SettingsInterface = DefaultXyoSdkSettings()): XyoSdk {
            synchronized(this) {
                // Initialize the singleton with the users accountPreferences
                AccountPrefsRepository.getInstance(context.applicationContext, settings)
                INSTANCE = XyoSdk(context.applicationContext, settings)
            }
            return INSTANCE!!
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}