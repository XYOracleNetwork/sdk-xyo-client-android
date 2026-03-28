package network.xyo.client.module.model

import network.xyo.client.account.model.AccountInstance
import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.payload.Payload

/**
 * Module status values matching JS ModuleStatus.
 * Per the XYO Yellow Paper Section 10.9.
 */
enum class ModuleStatus {
    CREATING,
    CREATED,
    STARTING,
    STARTED,
    STOPPING,
    STOPPED,
    ERROR,
    WRAPPED,
    PROXY
}

/**
 * Base module interface matching JS ModuleInstance.
 * Per the XYO Yellow Paper Section 10.3.
 *
 * All XYO module types (Witness, Diviner, Archivist, Sentinel, Bridge, Node)
 * extend this interface to provide a uniform query and lifecycle API.
 */
interface ModuleInstance {
    /** The account associated with this module. */
    val account: AccountInstance

    /** The module's address (hex string). */
    val address: String

    /** Optional human-readable name for this module. */
    val modName: String?
        get() = null

    /** The module's identifier: modName if set, otherwise address. */
    val id: String
        get() = modName ?: address

    /** The list of query schemas this module supports. */
    val queries: List<String>

    /** The current module status. */
    val status: ModuleStatus?
        get() = null

    /** The module's previous hash (from its account). */
    suspend fun previousHash(): String? {
        return null
    }

    /** Get a description of this module. */
    suspend fun describe(): ModuleDescription

    /**
     * Send a query to this module.
     * Returns a pair of the resulting BoundWitness and response payloads.
     */
    suspend fun query(query: Payload, payloads: List<Payload>? = null): Pair<BoundWitness, List<Payload>>

    /** Check if a query is supported. */
    fun isSupportedQuery(schema: String): Boolean {
        return schema in queries
    }

    /** Lifecycle: start the module. */
    suspend fun start() {}

    /** Lifecycle: stop the module. */
    suspend fun stop() {}
}
