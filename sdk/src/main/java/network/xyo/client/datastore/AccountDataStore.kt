package network.xyo.client.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import network.xyo.client.settings.DefaultXyoSdkSettings
import network.xyo.data.PrefsDataStoreProtos.PrefsDataStore
import java.io.File

val defaults = DefaultXyoSdkSettings()

fun Context.xyoAccountDataStore(name: String?, path: String?): DataStore<PrefsDataStore> {
    val resolvedName = name ?: defaults.accountPreferences.fileName
    val resolvedPath = path ?: defaults.accountPreferences.storagePath

    val dataStoreFile = File(filesDir, "$resolvedPath/$resolvedName")

    return MultiProcessDataStoreFactory.create(
        serializer = PrefsDataStoreSerializer,
        produceFile = { dataStoreFile },
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { PrefsDataStore.getDefaultInstance() }
        ),
        scope = CoroutineScope(Dispatchers.IO)
    )
}