package network.xyo.client.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.network.xyo.client.data.PrefsDataStoreProtos.PrefsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    fun getAccountKey(): Flow<String> {
        return this.prefsDataStore.data.map { prefs ->
            prefs.accountKey
        }
    }

    suspend fun setAccountKey(accountKey: String): PrefsDataStore {
        return this.prefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setAccountKey(accountKey)
            currentPrefs
        }
    }
}