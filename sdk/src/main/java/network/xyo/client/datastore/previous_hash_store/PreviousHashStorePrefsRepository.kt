package network.xyo.client.datastore.previous_hash_store

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import network.xyo.client.account.hexStringToByteArray
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.settings.PreviousHashStorePreferences
import network.xyo.client.settings.SettingsInterface
import network.xyo.client.settings.defaultXyoSdkSettings
import network.xyo.client.xyoScope
import network.xyo.data.PreviousHashPrefsDataStoreProtos.PreviousHashPrefsDataStore


class PreviousHashStorePrefsRepository(
    context: Context,
    settings: SettingsInterface = defaultXyoSdkSettings
): PreviousHashStore {
    private val appContext = context.applicationContext
    private val previousHashStorePreferences: PreviousHashStorePreferences = settings.previousHashStorePreferences

    // This should set the proper paths for the prefs datastore each time the the class is instantiated
    @Volatile
    private var previousHashStorePrefsDataStore: DataStore<PreviousHashPrefsDataStore> = appContext.xyoPreviousHashDataStore(
        previousHashStorePreferences.fileName, previousHashStorePreferences.storagePath
    )

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun getItem(address: ByteArray): ByteArray? {
        var savedPreviousHash: String? = null
        val job = xyoScope.launch {
            val savedPreviousHashStore = previousHashStorePrefsDataStore.data.first().addressToHashMap
            val searchKey = address.toHexString()
            savedPreviousHash = savedPreviousHashStore[searchKey]
        }
        job.join()
        return if (savedPreviousHash != null) {
            hexStringToByteArray(savedPreviousHash!!)
        } else {
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun setItem(address: ByteArray, previousHash: ByteArray) {
        val addressString = address.toHexString()
        val previousHashString = previousHash.toHexString()
        val job = xyoScope.launch {
            this@PreviousHashStorePrefsRepository.previousHashStorePrefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .putAddressToHash(addressString, previousHashString)
                    .build()
            }
        }
        job.join()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun removeItem(address: ByteArray) {
        val addressString = address.toHexString()
        val job = xyoScope.launch {
            this@PreviousHashStorePrefsRepository.previousHashStorePrefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .removeAddressToHash(addressString)
                    .build()
            }
        }
        job.join()
    }

    suspend fun clearStore() {
        val job = xyoScope.launch {
            this@PreviousHashStorePrefsRepository.previousHashStorePrefsDataStore.updateData { currentPrefs ->
                currentPrefs.toBuilder()
                    .clearAddressToHash()
                    .build()
            }
        }
        job.join()
    }



    companion object {
        @Volatile
        private var INSTANCE: PreviousHashStorePrefsRepository? = null

        // Method to retrieve the singleton instance
        fun getInstance(context: Context, settings: SettingsInterface): PreviousHashStorePrefsRepository {
            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreviousHashStorePrefsRepository(context.applicationContext, settings).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, settings: SettingsInterface): PreviousHashStorePrefsRepository {
            synchronized(this) {
                INSTANCE = PreviousHashStorePrefsRepository(context.applicationContext, settings)
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