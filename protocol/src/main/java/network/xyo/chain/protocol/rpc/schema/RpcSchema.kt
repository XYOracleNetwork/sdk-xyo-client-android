package network.xyo.chain.protocol.rpc.schema

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

/**
 * Defines how to serialize params and deserialize results for a single RPC method.
 * Mirrors the RequestResponseSchemas pattern from xl1-protocol.
 *
 * @param TResult The expected return type from the RPC method.
 */
class RpcSchema<TResult>(
    val resultType: Type,
    val resultTransform: ((Any?) -> TResult)? = null,
) {
    fun resultAdapter(moshi: Moshi): JsonAdapter<TResult> {
        @Suppress("UNCHECKED_CAST")
        return moshi.adapter<TResult>(resultType)
    }

    fun parseResult(moshi: Moshi, raw: Any?): TResult {
        if (resultTransform != null) {
            return resultTransform.invoke(raw)
        }
        return resultAdapter(moshi).fromJsonValue(raw)
            ?: throw IllegalArgumentException("Failed to deserialize RPC result to $resultType")
    }

    fun serializeResult(moshi: Moshi, value: Any?): Any? {
        @Suppress("UNCHECKED_CAST")
        val adapter = moshi.adapter<Any>(resultType) as JsonAdapter<Any?>
        return adapter.toJsonValue(value)
    }
}

/** A map of RPC method names to their schema definitions. */
typealias RpcSchemaMap = Map<String, RpcSchema<*>>

/** Builder DSL for constructing an RpcSchemaMap. */
class RpcSchemaMapBuilder {
    @PublishedApi
    internal val schemas = mutableMapOf<String, RpcSchema<*>>()

    inline fun <reified TResult> method(name: String) {
        schemas[name] = RpcSchema<TResult>(
            resultType = TResult::class.java,
        )
    }

    inline fun <reified TResult> method(name: String, noinline transform: (Any?) -> TResult) {
        schemas[name] = RpcSchema<TResult>(
            resultType = TResult::class.java,
            resultTransform = transform,
        )
    }

    fun build(): RpcSchemaMap = schemas.toMap()
}

inline fun rpcSchemaMap(block: RpcSchemaMapBuilder.() -> Unit): RpcSchemaMap {
    return RpcSchemaMapBuilder().apply(block).build()
}

/** Shared Moshi instance for RPC schema operations. */
val rpcMoshi: Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()
