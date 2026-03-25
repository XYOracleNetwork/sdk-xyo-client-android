package network.xyo.chain.protocol.rpc.schema

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import network.xyo.client.payload.Payload
import network.xyo.client.payload.model.Payload as PayloadInterface
import java.lang.reflect.Type

/**
 * Moshi adapter factory that routes the Payload interface to the concrete Payload class.
 * This allows Moshi to deserialize JSON objects with a "schema" field into Payload instances
 * when the declared type is the Payload interface.
 */
class PayloadJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (type == PayloadInterface::class.java) {
            return moshi.adapter(Payload::class.java)
        }
        return null
    }
}
