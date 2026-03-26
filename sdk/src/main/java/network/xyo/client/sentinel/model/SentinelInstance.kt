package network.xyo.client.sentinel.model

import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.module.model.ModuleInstance
import network.xyo.client.payload.Payload

/**
 * Sentinel module interface matching JS SentinelInstance.
 *
 * Sentinels orchestrate witnesses, collect observations, and report to nodes.
 * This is the module-system equivalent of the legacy XyoPanel class.
 */
interface SentinelInstance : ModuleInstance {
    /**
     * Execute a report cycle: observe from all witnesses, build a bound witness, and return results.
     * @return pair of the bound witness and the collected payloads.
     */
    suspend fun report(payloads: List<Payload>? = null): Pair<BoundWitness, List<Payload>>
}
