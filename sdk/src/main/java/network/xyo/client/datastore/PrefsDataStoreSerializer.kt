package network.xyo.client.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import network.xyo.data.PrefsDataStoreProtos.PrefsDataStore
import java.io.InputStream
import java.io.OutputStream

const val DATA_STORE_FILE_NAME = "network-xyo-sdk-prefs.pb"

val Context.xyoAccountDataStore: DataStore<PrefsDataStore> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = PrefsDataStoreSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = { PrefsDataStore.getDefaultInstance() }
    )
)

object PrefsDataStoreSerializer : Serializer<PrefsDataStore> {
    override val defaultValue: PrefsDataStore = PrefsDataStore.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): PrefsDataStore {
        try {
            return PrefsDataStore.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PrefsDataStore, output: OutputStream) = t.writeTo(output)
}