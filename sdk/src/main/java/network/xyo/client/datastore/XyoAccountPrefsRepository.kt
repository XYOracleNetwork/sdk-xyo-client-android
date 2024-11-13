package network.xyo.client.datastore

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import network.xyo.data.PrefsDataStoreProtos.PrefsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import network.xyo.client.address.XyoAccount
import network.xyo.client.settings.AccountPreferences
import network.xyo.client.xyoScope
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class XyoAccountPrefsRepository(context: Context, private val accountPreferences: AccountPreferences = defaults.accountPreferences) {
    @Volatile
    private var prefsDataStore: DataStore<PrefsDataStore> = context.xyoAccountDataStore(
        accountPreferences.fileName, accountPreferences.storagePath
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun prefsFileAtPath(path: String, context: Context): File? {
        var existingFile: File? = null
        val job = xyoScope.launch {
            val existingFilePath = Paths.get(context.filesDir.toString(), path)
            if (Files.exists(existingFilePath)) {
                existingFile = File(context.filesDir.toString(), path)
            } else {
                Log.w("xyoClient", "Unable to locate prefs at $existingFilePath")
                existingFile = null
            }
        }
        job.join()
        return existingFile
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun migrate(context: Context, accountPreferences: AccountPreferences) {
        val existingAccountPreferences = this@XyoAccountPrefsRepository.accountPreferences
        val existingPath = "${existingAccountPreferences.storagePath}/${existingAccountPreferences.fileName}"
        val updatedPath = "${accountPreferences.storagePath}/${accountPreferences.fileName}"
        if (existingPath !== updatedPath) {
            migrateAccountToNewLocation(context, accountPreferences, existingPath, updatedPath)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun migrateAccountToNewLocation(context: Context, accountPreferences: AccountPreferences, existingPath: String, updatedPath: String) {
        val job = xyoScope.launch {
            val existingFile = prefsFileAtPath(existingPath, context)
            if (existingFile == null) {
                Log.e("xyoClient", "Unable to locate existing prefs: $existingPath ")
            } else {
                try {
                    val sourcePath = existingFile.toPath()
                    val destinationPath = File(context.filesDir, updatedPath).toPath()

                    // Move the existing file to the new location
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING)

                    synchronized(this) {
                        val newInstance = getInstance(context, accountPreferences)
                        newInstance.also { INSTANCE = it }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("xyoClient", "Error Moving File: ${e.message}")
                }
            }
        }
        job.join()
    }

    // Define the singleton instance within a companion object
    companion object {
        @Volatile
        private var INSTANCE: XyoAccountPrefsRepository? = null

        // Method to retrieve the singleton instance
        fun getInstance(context: Context, accountPreferences: AccountPreferences = defaults.accountPreferences): XyoAccountPrefsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: XyoAccountPrefsRepository(context, accountPreferences).also { INSTANCE = it }
            }
        }

        fun refresh(context: Context, accountPreferences: AccountPreferences): XyoAccountPrefsRepository {
            return synchronized(this) {
                val newInstance = getInstance(context, accountPreferences)
                newInstance.also { INSTANCE = it }
            }
        }
    }
}