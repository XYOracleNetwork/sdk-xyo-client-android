package network.xyo.client.module.model

import network.xyo.client.account.model.AccountInstance
import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.payload.Payload

/**
 * Base module interface matching JS ModuleInstance.
 *
 * All XYO module types (Witness, Diviner, Archivist, Sentinel, Bridge, Node)
 * extend this interface to provide a uniform query and lifecycle API.
 */
interface ModuleInstance {
    /** The account associated with this module. */
    val account: AccountInstance

    /** The module's address (hex string). */
    val address: String

    /** The list of query schemas this module supports. */
    val queries: List<String>

    /** Get a description of this module. */
    suspend fun describe(): ModuleDescription

    /**
     * Send a query to this module.
     * Returns a pair of the resulting BoundWitness and response payloads.
     */
    suspend fun query(query: Payload, payloads: List<Payload>? = null): Pair<BoundWitness, List<Payload>>

    /** Optional lifecycle: start the module. */
    suspend fun start() {}

    /** Optional lifecycle: stop the module. */
    suspend fun stop() {}
}
