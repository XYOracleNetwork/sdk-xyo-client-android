package network.xyo.client.witness.model

import network.xyo.client.module.model.ModuleInstance
import network.xyo.client.payload.Payload

/**
 * Witness module interface matching JS WitnessInstance.
 *
 * Witnesses observe the world and produce payloads.
 */
interface WitnessInstance : ModuleInstance {
    /**
     * Observe and produce payloads.
     * @param payloads optional input payloads to inform the observation.
     * @return list of observed payloads.
     */
    suspend fun observe(payloads: List<Payload>? = null): List<Payload>
}
