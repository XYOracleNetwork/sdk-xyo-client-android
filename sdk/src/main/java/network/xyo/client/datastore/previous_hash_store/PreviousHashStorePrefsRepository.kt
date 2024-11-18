package network.xyo.client.datastore.previous_hash_store

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import network.xyo.client.account.hexStringToByteArray
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.settings.PreviousHashStorePreferences
import network.xyo.client.settings.defaultXyoSdkSettings
import network.xyo.client.xyoScope
import network.xyo.data.PreviousHashPrefsDataStoreProtos.PreviousHashPrefsDataStore


class PreviousHashStorePrefsRepository(
    context: Context,
    private val _previousHashStorePreferences: PreviousHashStorePreferences = defaultXyoSdkSettings.previousHashStorePreferences
): PreviousHashStore {
    private val appContext = context.applicationContext

    // This should set the proper paths for the prefs datastore each time the the class is instantiated
    @Volatile
    private var previousHashStorePrefsDataStore: DataStore<PreviousHashPrefsDataStore> = appContext.xyoPreviousHashDataStore(
        previousHashStorePreferences.fileName, previousHashStorePreferences.storagePath
    )

    // Exposing as a getter so path/filename preferences can be fetched from a separate location if needed.
    val previousHashStorePreferences: PreviousHashStorePreferences
        get() = _previousHashStorePreferences

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

    suspend override fun setItem(address: ByteArray, previousHash: ByteArray) {
        TODO("Not yet implemented")
    }

    suspend override fun removeItem(address: ByteArray) {
        TODO("Not yet implemented")
    }


//    @OptIn(ExperimentalStdlibApi::class)
//    @RequiresApi(Build.VERSION_CODES.M)
//    suspend fun initializeAccount(account: AccountInstance): AccountInstance? {
//        var updatedKey: String? = null
//        val job = xyoScope.launch {
//            val savedKey = accountPrefsDataStore.data.first().accountKey
//            if (savedKey.isNullOrEmpty()) {
//                // no saved key so save the passed in one
//                updatedKey = null
//                setAccountKey(account.privateKey.toHexString())
//            } else {
//                updatedKey = null
//                Log.w("xyoClient", "Key already exists.  Clear it first before initializing prefs with new account")
//            }
//        }
//        job.join()
//        return if (updatedKey !== null) {
//            account
//        } else {
//            null
//        }
//    }

//    @OptIn(ExperimentalStdlibApi::class)
//    @RequiresApi(Build.VERSION_CODES.M)
//    private suspend fun getAccountKey(): String {
//        val savedKey = accountPrefsDataStore.data.first().accountKey
//        return if (savedKey.isEmpty()) {
//            val newAccount: AccountInstance = Account.random()
//            setAccountKey(newAccount.privateKey.toHexString())
//            newAccount.privateKey.toHexString()
//        } else {
//            return savedKey
//        }
//    }

//    private suspend fun setAccountKey(accountKey: String): DataStore<AccountPrefsDataStore> {
//        val job = xyoScope.launch {
//            this@PreviousHashStorePrefsRepository.accountPrefsDataStore.updateData { currentPrefs ->
//                currentPrefs.toBuilder()
//                    .setAccountKey(accountKey)
//                    .build()
//            }
//        }
//        job.join()
//        return accountPrefsDataStore
//    }
//
//    suspend fun clearSavedAccountKey(): DataStore<AccountPrefsDataStore> {
//        val job = xyoScope.launch {
//            this@PreviousHashStorePrefsRepository.accountPrefsDataStore.updateData { currentPrefs ->
//                currentPrefs.toBuilder()
//                    .setAccountKey("")
//                    .build()
//            }
//        }
//        job.join()
//        return accountPrefsDataStore
//    }

    companion object {
        @Volatile
        private var INSTANCE: PreviousHashStorePrefsRepository? = null

        // Method to retrieve the singleton instance
        fun getInstance(context: Context, previousHashStorePreferences: PreviousHashStorePreferences = defaultXyoSdkSettings.previousHashStorePreferences): PreviousHashStorePrefsRepository {
            val newInstance = INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreviousHashStorePrefsRepository(context.applicationContext, previousHashStorePreferences).also { INSTANCE = it }
            }
            return newInstance
        }

        fun refresh(context: Context, previousHashStorePreferences: PreviousHashStorePreferences): PreviousHashStorePrefsRepository {
            synchronized(this) {
                INSTANCE = PreviousHashStorePrefsRepository(context.applicationContext, previousHashStorePreferences)
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