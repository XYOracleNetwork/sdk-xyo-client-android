package network.xyo.client.diviner.model

import network.xyo.client.module.model.ModuleInstance
import network.xyo.client.payload.Payload

/**
 * Diviner module interface matching JS DivinerInstance.
 *
 * Diviners analyze and transform payloads.
 */
interface DivinerInstance : ModuleInstance {
    /**
     * Divine (analyze/transform) the given payloads.
     * @param payloads input payloads to analyze.
     * @return list of result payloads.
     */
    suspend fun divine(payloads: List<Payload>? = null): List<Payload>
}
