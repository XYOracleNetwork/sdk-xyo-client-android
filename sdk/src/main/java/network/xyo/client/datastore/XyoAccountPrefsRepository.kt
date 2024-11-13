package network.xyo.client.datastore

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import network.xyo.data.PrefsDataStoreProtos.PrefsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import network.xyo.client.address.XyoAccount
import network.xyo.client.settings.AccountPreferences
import network.xyo.client.xyoScope


class XyoAccountPrefsRepository(context: Context, private val _accountPreferences: AccountPreferences = defaults.accountPreferences) {
    // This should set the proper paths for the prefs datastore each time the the class is instantiated
    @Volatile
    private var prefsDataStore: DataStore<PrefsDataStore> = context.xyoAccountDataStore(
        accountPreferences.fileName, accountPreferences.storagePath
    )

    // Exposing as a getter so path/filename preferences can be fetched from a separate location if needed.
    val accountPreferences: AccountPreferences
        get() = _accountPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getAccount(): XyoAccount {
        val savedKeyBytes = getAccountKey().encodeToByteArray()
        return XyoAccount(savedKeyBytes)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getAccountKey(): String {
        val savedKey = prefsDataStore.data.first().accountKey
        return if (savedKey.isEmpty()) {
            val newAccount = XyoAccount()
            setAccountKey(newAccount.private.hex)
            newAccount.private.hex
        } else {
            return savedKey
        }
    }

    private suspend fun setAccountKey(accountKey: String): DataStore<PrefsDataStore> {
        val job = xyoScope.launch {
            this@XyoAccountPrefsRepository.prefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .setAccountKey(accountKey)
                    .build()
            }
        }
        job.join()
        return prefsDataStore
    }

    suspend fun clearSavedAccountKey(): DataStore<PrefsDataStore> {
        val job = xyoScope.launch {
            this@XyoAccountPrefsRepository.prefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .setAccountKey("")
                    .build()
            }
        }
        job.join()
        return prefsDataStore
    }

    // Define the singleton instance within a companion object
    companion object {
        @Volatile
        private var INSTANCE: XyoAccountPrefsRepository? = null

        // Method to retrieve the singleton instance
        fun getInstance(context: Context, accountPreferences: AccountPreferences = defaults.accountPreferences): XyoAccountPrefsRepository {
            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: XyoAccountPrefsRepository(context, accountPreferences).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, accountPreferences: AccountPreferences): XyoAccountPrefsRepository {
            synchronized(this) {
                INSTANCE = XyoAccountPrefsRepository(context, accountPreferences)
            }
            return INSTANCE!! // Return the updated instance
        }
    }
}