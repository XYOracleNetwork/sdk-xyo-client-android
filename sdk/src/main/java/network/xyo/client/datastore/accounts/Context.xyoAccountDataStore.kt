package network.xyo.client.datastore.accounts

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import network.xyo.client.settings.defaultXyoSdkSettings
import network.xyo.data.AccountPrefsDataStoreProtos.AccountPrefsDataStore
import java.io.File

fun Context.xyoAccountDataStore(name: String?, path: String?): DataStore<AccountPrefsDataStore> {
    val resolvedName = name ?: defaultXyoSdkSettings.accountPreferences.fileName
    val resolvedPath = path ?: defaultXyoSdkSettings.accountPreferences.storagePath

    val dataStoreFile = File(filesDir, "$resolvedPath/$resolvedName")

    return MultiProcessDataStoreFactory.create(
        serializer = AccountPrefsDataStoreSerializer,
        produceFile = { dataStoreFile },
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { AccountPrefsDataStore.getDefaultInstance() }
        ),
        scope = CoroutineScope(Dispatchers.IO)
    )
}