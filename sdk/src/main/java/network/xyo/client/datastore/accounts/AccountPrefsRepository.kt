package network.xyo.client.datastore.accounts

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import network.xyo.data.AccountPrefsDataStoreProtos.AccountPrefsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.settings.AccountPreferences
import network.xyo.client.settings.defaultXyoSdkSettings
import network.xyo.client.xyoScope


class AccountPrefsRepository(context: Context, private val _accountPreferences: AccountPreferences = defaultXyoSdkSettings.accountPreferences) {
    private val appContext = context.applicationContext

    // This should set the proper paths for the prefs datastore each time the the class is instantiated
    @Volatile
    private var accountPrefsDataStore: DataStore<AccountPrefsDataStore> = appContext.xyoAccountDataStore(
        accountPreferences.fileName, accountPreferences.storagePath
    )

    // Exposing as a getter so path/filename preferences can be fetched from a separate location if needed.
    val accountPreferences: AccountPreferences
        get() = _accountPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getAccount(): AccountInstance {
        val saveKeyHex = getAccountKey()
        return Account.fromPrivateKey(saveKeyHex)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun initializeAccount(account: AccountInstance): AccountInstance? {
        var updatedKey: String? = null
        val job = xyoScope.launch {
            val savedKey = accountPrefsDataStore.data.first().accountKey
            if (savedKey.isNullOrEmpty()) {
                // no saved key so save the passed in one
                updatedKey = null
                setAccountKey(account.privateKey.toHexString())
            } else {
                updatedKey = null
                Log.w("xyoClient", "Key already exists.  Clear it first before initializing prefs with new account")
            }
        }
        job.join()
        return if (updatedKey !== null) {
            account
        } else {
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getAccountKey(): String {
        val savedKey = accountPrefsDataStore.data.first().accountKey
        return if (savedKey.isEmpty()) {
            val newAccount: AccountInstance = Account.random()
            setAccountKey(newAccount.privateKey.toHexString())
            newAccount.privateKey.toHexString()
        } else {
            return savedKey
        }
    }

    private suspend fun setAccountKey(accountKey: String): DataStore<AccountPrefsDataStore> {
        val job = xyoScope.launch {
            this@AccountPrefsRepository.accountPrefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .setAccountKey(accountKey)
                    .build()
            }
        }
        job.join()
        return accountPrefsDataStore
    }

    suspend fun clearSavedAccountKey(): DataStore<AccountPrefsDataStore> {
        val job = xyoScope.launch {
            this@AccountPrefsRepository.accountPrefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .setAccountKey("")
                    .build()
            }
        }
        job.join()
        return accountPrefsDataStore
    }

    companion object {
        @Volatile
        private var INSTANCE: AccountPrefsRepository? = null

        // Method to retrieve the singleton instance
        fun getInstance(context: Context, accountPreferences: AccountPreferences = defaultXyoSdkSettings.accountPreferences): AccountPrefsRepository {
            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: AccountPrefsRepository(context.applicationContext, accountPreferences).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, accountPreferences: AccountPreferences): AccountPrefsRepository {
            synchronized(this) {
                INSTANCE = AccountPrefsRepository(context.applicationContext, accountPreferences)
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