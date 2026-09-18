package network.xyo.chain.protocol.rpc.schema

import network.xyo.chain.protocol.payload.elevatable.TimePayload
import network.xyo.client.payload.Payload

internal fun parseRpcPayloads(payloadsList: List<Map<String, Any?>>): List<Payload> {
    return payloadsList.map { payloadMap ->
        val schema = payloadMap["schema"] as? String ?: "unknown"
        when (schema) {
            TimePayload.SCHEMA -> runCatching {
                rpcMoshi.adapter(TimePayload::class.java).fromJsonValue(payloadMap)
            }.getOrNull() ?: Payload(TimePayload.SCHEMA)
            else -> runCatching {
                rpcMoshi.adapter(Payload::class.java).fromJsonValue(payloadMap)
            }.getOrNull() ?: Payload(schema)
        }
    }
}
