package network.xyo.client.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import network.xyo.client.settings.DefaultXyoSdkSettings
import network.xyo.data.PrefsDataStoreProtos.PrefsDataStore
import java.io.File

val defaults = DefaultXyoSdkSettings()

fun Context.xyoAccountDataStore(name: String? = defaults.accountPreferences.fileName, path: String? = defaults.accountPreferences.storagePath): DataStore<PrefsDataStore> {
    val dataStoreFile = File(filesDir, "$path/$name")

    return DataStoreFactory.create(
        serializer = PrefsDataStoreSerializer,
        produceFile = { dataStoreFile },
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { PrefsDataStore.getDefaultInstance() }
        ),
        scope = CoroutineScope(Dispatchers.IO)
    )
}