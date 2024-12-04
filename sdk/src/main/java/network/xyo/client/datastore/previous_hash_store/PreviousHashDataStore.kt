package network.xyo.client.datastore.previous_hash_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import network.xyo.client.datastore.accounts.AccountPrefsDataStoreSerializer
import network.xyo.client.settings.defaultXyoSdkSettings
import network.xyo.data.AccountPrefsDataStoreProtos.AccountPrefsDataStore
import network.xyo.data.PreviousHashPrefsDataStoreProtos
import network.xyo.data.PreviousHashPrefsDataStoreProtos.PreviousHashPrefsDataStore
import java.io.File

fun Context.xyoPreviousHashDataStore(name: String?, path: String?): DataStore<PreviousHashPrefsDataStore> {
    val resolvedName = name ?: defaultXyoSdkSettings.previousHashStorePreferences.fileName
    val resolvedPath = path ?: defaultXyoSdkSettings.previousHashStorePreferences.storagePath

    val dataStoreFile = File(filesDir, "$resolvedPath/$resolvedName")

    return MultiProcessDataStoreFactory.create(
        serializer = PreviousHashStorePrefsDataStoreSerializer,
        produceFile = { dataStoreFile },
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { PreviousHashPrefsDataStore.getDefaultInstance() }
        ),
        scope = CoroutineScope(Dispatchers.IO)
    )
}