package network.xyo.client.module

import network.xyo.client.boundwitness.BoundWitness
import network.xyo.client.payload.Payload
import network.xyo.client.payload.types.ModuleErrorPayload

/**
 * Standard return type for all module queries, matching JS ModuleQueryResult.
 * Per the XYO Yellow Paper Section 10.1.
 *
 * Components: [response BoundWitness, result payloads, error payloads]
 */
typealias ModuleQueryResult<T> = Triple<BoundWitness, List<T>, List<ModuleErrorPayload>>

/** Convenience alias using base Payload type. */
typealias ModuleQueryPayloadResult = ModuleQueryResult<Payload>