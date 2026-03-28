package network.xyo.client.module

import network.xyo.client.archivist.model.ArchivistInstance
import network.xyo.client.bridge.model.BridgeInstance
import network.xyo.client.diviner.model.DivinerInstance
import network.xyo.client.module.model.ModuleInstance
import network.xyo.client.node.model.NodeInstance
import network.xyo.client.payload.Payload
import network.xyo.client.sentinel.model.SentinelInstance
import network.xyo.client.witness.model.WitnessInstance

/**
 * Type check functions for module instances.
 * Per the XYO Yellow Paper Section 16.3.
 *
 * In Kotlin, these supplement the built-in `is` operator by providing
 * nullable-safe casting (as* functions) and schema-based payload checks.
 */
object ModuleTypeChecks {

    // --- Module instance type checks ---

    fun isModuleInstance(value: Any?): Boolean = value is ModuleInstance

    fun asModuleInstance(value: Any?): ModuleInstance? = value as? ModuleInstance

    fun isArchivistInstance(value: Any?): Boolean = value is ArchivistInstance

    fun asArchivistInstance(value: Any?): ArchivistInstance? = value as? ArchivistInstance

    fun isDivinerInstance(value: Any?): Boolean = value is DivinerInstance

    fun asDivinerInstance(value: Any?): DivinerInstance? = value as? DivinerInstance

    fun isWitnessInstance(value: Any?): Boolean = value is WitnessInstance

    fun asWitnessInstance(value: Any?): WitnessInstance? = value as? WitnessInstance

    fun isSentinelInstance(value: Any?): Boolean = value is SentinelInstance

    fun asSentinelInstance(value: Any?): SentinelInstance? = value as? SentinelInstance

    fun isBridgeInstance(value: Any?): Boolean = value is BridgeInstance

    fun asBridgeInstance(value: Any?): BridgeInstance? = value as? BridgeInstance

    fun isNodeInstance(value: Any?): Boolean = value is NodeInstance

    fun asNodeInstance(value: Any?): NodeInstance? = value as? NodeInstance

    // --- Payload schema type checks ---

    /** Check if a payload has a specific schema. */
    fun isPayloadOfSchema(value: Any?, schema: String): Boolean {
        return value is Payload && value.schema == schema
    }

    /** Create a reusable schema-based type check function. */
    fun payloadSchemaCheck(schema: String): (Any?) -> Boolean {
        return { value -> isPayloadOfSchema(value, schema) }
    }

    // Pre-built schema checks for common payload types
    val isModuleError = payloadSchemaCheck("network.xyo.error.module")
    val isBoundWitness = payloadSchemaCheck("network.xyo.boundwitness")
    val isIdPayload = payloadSchemaCheck("network.xyo.id")
    val isAddressPayload = payloadSchemaCheck("network.xyo.address")
}
