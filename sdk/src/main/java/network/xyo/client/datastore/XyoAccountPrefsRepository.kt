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

class XyoAccountPrefsRepository(context: Context, accountPreferences: AccountPreferences?) {
    constructor(context: Context): this(context, null)

    private val prefsDataStore: DataStore<PrefsDataStore> = context.xyoAccountDataStore(
        accountPreferences?.fileName, accountPreferences?.storagePath
    )

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
}