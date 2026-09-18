package network.xyo.client.android.datastore.accounts

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import network.xyo.data.AccountPrefsDataStoreProtos.AccountPrefsDataStore
import kotlinx.coroutines.flow.first
import network.xyo.client.account.Account
import network.xyo.client.settings.AccountPreferences
import network.xyo.client.settings.SettingsInterface
import network.xyo.client.settings.defaultXyoSdkSettings


class AccountPrefsRepository(context: Context, settings: SettingsInterface = defaultXyoSdkSettings) {
    private val appContext = context.applicationContext
    val accountPreferences: AccountPreferences = settings.accountPreferences

    // This should set the proper paths for the prefs datastore each time the the class is instantiated
    @Volatile
    private var accountPrefsDataStore: DataStore<AccountPrefsDataStore> = appContext.xyoAccountDataStore(
        accountPreferences.fileName, accountPreferences.storagePath
    )

    suspend fun getAccount(): network.xyo.client.account.model.Account {
        val saveKeyHex = getAccountKey()
        return Account.fromPrivateKey(saveKeyHex)
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun initializeAccount(account: network.xyo.client.account.model.Account): network.xyo.client.account.model.Account? {
        val savedKey = accountPrefsDataStore.data.first().accountKey
        return if (savedKey.isNullOrEmpty()) {
            setAccountKey(account.privateKey.toHexString())
            account
        } else {
            Log.w("xyoClient", "Key already exists.  Clear it first before initializing prefs with new account")
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun getAccountKey(): String {
        val savedKey = accountPrefsDataStore.data.first().accountKey
        return if (savedKey.isEmpty()) {
            val newAccount: network.xyo.client.account.model.Account = Account.random()
            setAccountKey(newAccount.privateKey.toHexString())
            newAccount.privateKey.toHexString()
        } else {
            savedKey
        }
    }

    private suspend fun setAccountKey(accountKey: String): DataStore<AccountPrefsDataStore> {
        accountPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setAccountKey(accountKey)
                .build()
        }
        return accountPrefsDataStore
    }

    suspend fun clearSavedAccountKey(): DataStore<AccountPrefsDataStore> {
        accountPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setAccountKey("")
                .build()
        }
        return accountPrefsDataStore
    }

    companion object {
        @Volatile
        private var INSTANCE: AccountPrefsRepository? = null

        fun getInstance(context: Context, settings: SettingsInterface = defaultXyoSdkSettings): AccountPrefsRepository {
            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: AccountPrefsRepository(context.applicationContext, settings).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, settings: SettingsInterface = defaultXyoSdkSettings): AccountPrefsRepository {
            synchronized(this) {
                INSTANCE = AccountPrefsRepository(context.applicationContext, settings)
                return INSTANCE!!
            }
        }

        fun resetInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }
    }
}