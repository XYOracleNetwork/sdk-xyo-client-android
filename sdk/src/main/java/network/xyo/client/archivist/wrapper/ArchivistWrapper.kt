package network.xyo.client.archivist.wrapper

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.Payload

open class ArchivistWrapper(private val nodeClient: NodeClient) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun get(hashes: List<String>): PostQueryResult {
        val query = ArchivistGetQueryPayload(hashes)
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun insert(payloads: List<Payload>): PostQueryResult {
        val query = ArchivistInsertQueryPayload()
        return nodeClient.query(query, payloads)
    }
}