package network.xyo.client.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import network.xyo.data.AccountPrefsDataStoreProtos.AccountPrefsDataStore
import java.io.InputStream
import java.io.OutputStream

object AccountPrefsDataStoreSerializer : Serializer<AccountPrefsDataStore> {
    override val defaultValue: AccountPrefsDataStore = AccountPrefsDataStore.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): AccountPrefsDataStore {
        try {
            return AccountPrefsDataStore.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AccountPrefsDataStore, output: OutputStream) = t.writeTo(output)
}