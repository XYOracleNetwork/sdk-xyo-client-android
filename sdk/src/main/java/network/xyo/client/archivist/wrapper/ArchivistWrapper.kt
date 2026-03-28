package network.xyo.client.archivist.wrapper

import kotlinx.coroutines.ExperimentalCoroutinesApi
import network.xyo.client.node.client.NodeClient
import network.xyo.client.node.client.PostQueryResult
import network.xyo.client.payload.Payload

open class ArchivistWrapper(private val nodeClient: NodeClient) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun get(hashes: List<String>): PostQueryResult {
        val query = ArchivistGetQueryPayload(hashes)
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun insert(payloads: List<Payload>): PostQueryResult {
        val query = ArchivistInsertQueryPayload()
        return nodeClient.query(query, payloads)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun delete(hashes: List<String>): PostQueryResult {
        val query = ArchivistDeleteQueryPayload(hashes)
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun all(): PostQueryResult {
        val query = ArchivistAllQueryPayload()
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun clear(): PostQueryResult {
        val query = ArchivistClearQueryPayload()
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun next(
        cursor: String? = null,
        limit: Int? = null,
        open: Boolean? = null,
        order: String? = null
    ): PostQueryResult {
        val query = ArchivistNextQueryPayload(cursor, limit, open, order)
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun commit(): PostQueryResult {
        val query = ArchivistCommitQueryPayload()
        return nodeClient.query(query, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun snapshot(): PostQueryResult {
        val query = ArchivistSnapshotQueryPayload()
        return nodeClient.query(query, null)
    }
}