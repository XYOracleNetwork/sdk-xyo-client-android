package network.xyo.client.archivist.wrapper

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import network.xyo.client.XyoSerializable
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.XyoPayload

open class ArchivistWrapper(private val nodeClient: NodeClient) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun get(hashes: List<String>, previousHash: String?): PostQueryResult {
        val query = ArchivistGetQueryPayload(hashes)
        return nodeClient.query(query, null, previousHash)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun insert(payloads: List<XyoPayload>, previousHash: String?): PostQueryResult {
        val payloadHashes = arrayListOf<String>()
        payloads.forEach { payloadHashes.add(XyoSerializable.sha256String(it)) }

        val query = ArchivistInsertQueryPayload()
        return nodeClient.query(query, payloads, previousHash)
    }
}