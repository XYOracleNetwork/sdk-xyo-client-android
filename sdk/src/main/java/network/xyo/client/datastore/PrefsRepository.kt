package network.xyo.client.datastore

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.network.xyo.client.data.PrefsDataStoreProtos.PrefsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import network.xyo.client.address.XyoAccount
import network.xyo.client.xyoScope

const val DATA_STORE_FILE_NAME = "prefs.pb"

private val Context.prefsDataStore: DataStore<PrefsDataStore> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = PrefsDataStoreSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = { PrefsDataStore.getDefaultInstance() }
    )
)

class PrefsRepository {
    private val prefsDataStore: DataStore<PrefsDataStore>
    constructor(
        context: Context
    ) {
        this.prefsDataStore = context.prefsDataStore
    }

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getAccount(): XyoAccount {
        val savedKeyBytes = getAccountKey().encodeToByteArray()
        return XyoAccount(savedKeyBytes)
    }
    private suspend fun getAccountKey(): String {
        val savedKey = prefsDataStore.data.first().accountKey
        Log.d("xyoClient", "savedKey: $savedKey")
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
            this@PrefsRepository.prefsDataStore.updateData { currentPrefs ->
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
            this@PrefsRepository.prefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .setAccountKey("")
                    .build()
            }
        }
        job.join()
        return prefsDataStore
    }
}