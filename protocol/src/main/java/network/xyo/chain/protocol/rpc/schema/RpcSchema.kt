package network.xyo.chain.protocol.rpc.schema

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Defines how to serialize params and deserialize results for a single RPC method.
 * Mirrors the RequestResponseSchemas pattern from xl1-protocol.
 *
 * @param TResult The expected return type from the RPC method.
 */
class RpcSchema<TResult>(
    val paramsType: Type = LIST_OF_ANY_TYPE,
    val resultType: Type,
    val paramsTransform: ((Any?) -> List<Any?>)? = null,
    val paramsSerialize: ((List<Any?>) -> Any?)? = null,
    val resultTransform: ((Any?) -> TResult)? = null,
) {
    fun paramsAdapter(moshi: Moshi): JsonAdapter<List<Any?>> {
        @Suppress("UNCHECKED_CAST")
        return moshi.adapter<List<Any?>>(paramsType)
    }

    fun parseParams(moshi: Moshi, raw: Any?): List<Any?> {
        if (paramsTransform != null) {
            return paramsTransform.invoke(raw)
        }
        return paramsAdapter(moshi).fromJsonValue(raw) ?: emptyList()
    }

    fun serializeParams(moshi: Moshi, params: List<Any?>): Any? {
        if (paramsSerialize != null) {
            return paramsSerialize.invoke(params)
        }
        return paramsAdapter(moshi).toJsonValue(params)
    }

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

private val LIST_OF_ANY_TYPE: Type = object : TypeToken<List<Any?>>() {}.type

/**
 * Captures a reified type as a [Type], preserving generic type parameters.
 * Works by creating an anonymous subclass of TypeToken that Moshi's Types
 * utility can extract the full parameterized type from.
 */
abstract class TypeToken<T> {
    val type: Type
        get() {
            val superclass = javaClass.genericSuperclass as ParameterizedType
            return superclass.actualTypeArguments[0]
        }
}

/** Builder DSL for constructing an RpcSchemaMap. */
class RpcSchemaMapBuilder {
    @PublishedApi
    internal val schemas = mutableMapOf<String, RpcSchema<*>>()

    inline fun <reified TResult> method(name: String) {
        val type = object : TypeToken<TResult>() {}.type
        schemas[name] = RpcSchema<TResult>(resultType = type)
    }

    inline fun <reified TResult> method(name: String, noinline transform: (Any?) -> TResult) {
        val type = object : TypeToken<TResult>() {}.type
        schemas[name] = RpcSchema<TResult>(
            resultType = type,
            resultTransform = transform,
        )
    }

    inline fun <reified TResult> method(
        name: String,
        noinline paramsTransform: (Any?) -> List<Any?>,
        noinline paramsSerialize: (List<Any?>) -> Any?,
        noinline resultTransform: (Any?) -> TResult,
    ) {
        val type = object : TypeToken<TResult>() {}.type
        schemas[name] = RpcSchema(
            resultType = type,
            paramsTransform = paramsTransform,
            paramsSerialize = paramsSerialize,
            resultTransform = resultTransform,
        )
    }

    inline fun <reified TResult> method(
        name: String,
        noinline paramsTransform: (Any?) -> List<Any?>,
        noinline paramsSerialize: (List<Any?>) -> Any?,
    ) {
        val type = object : TypeToken<TResult>() {}.type
        schemas[name] = RpcSchema<TResult>(
            resultType = type,
            paramsTransform = paramsTransform,
            paramsSerialize = paramsSerialize,
        )
    }

    fun build(): RpcSchemaMap = schemas.toMap()
}

inline fun rpcSchemaMap(block: RpcSchemaMapBuilder.() -> Unit): RpcSchemaMap {
    return RpcSchemaMapBuilder().apply(block).build()
}

/** Shared Moshi instance for RPC schema operations. */
val rpcMoshi: Moshi = Moshi.Builder()
    .add(PayloadJsonAdapterFactory())
    .addLast(KotlinJsonAdapterFactory())
    .build()
