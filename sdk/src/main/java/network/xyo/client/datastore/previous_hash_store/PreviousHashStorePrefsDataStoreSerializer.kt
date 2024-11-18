package network.xyo.client.datastore.previous_hash_store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import network.xyo.data.PreviousHashPrefsDataStoreProtos.PreviousHashPrefsDataStore
import java.io.InputStream
import java.io.OutputStream

object PreviousHashStorePrefsDataStoreSerializer : Serializer<PreviousHashPrefsDataStore> {
    override val defaultValue: PreviousHashPrefsDataStore = PreviousHashPrefsDataStore.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): PreviousHashPrefsDataStore {
        try {
            return PreviousHashPrefsDataStore.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PreviousHashPrefsDataStore, output: OutputStream) = t.writeTo(output)
}