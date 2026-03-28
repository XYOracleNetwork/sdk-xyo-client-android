package network.xyo.client.payload

/**
 * Core protocol schema constants matching the XYO Yellow Paper Section 7.
 */
object Schemas {

    // Core protocol schemas - Section 7.1
    const val PAYLOAD = "network.xyo.payload"
    const val BOUND_WITNESS = "network.xyo.boundwitness"
    const val MODULE_ERROR = "network.xyo.error.module"
    const val PAYLOAD_BUNDLE = "network.xyo.payload.bundle"
    const val PAYLOAD_SET = "network.xyo.payload.set"

    // Module config schemas - Section 7.2
    const val MODULE_CONFIG = "network.xyo.module.config"
    const val ARCHIVIST_CONFIG = "network.xyo.archivist.config"
    const val DIVINER_CONFIG = "network.xyo.diviner.config"
    const val WITNESS_CONFIG = "network.xyo.witness.config"
    const val SENTINEL_CONFIG = "network.xyo.sentinel.config"
    const val BRIDGE_CONFIG = "network.xyo.bridge.config"
    const val NODE_CONFIG = "network.xyo.node.config"

    // Module payload schemas - Section 7.3
    const val ADDRESS = "network.xyo.address"
    const val ADDRESS_CHILD = "network.xyo.address.child"
    const val ADDRESS_HASH_PREVIOUS = "network.xyo.address.hash.previous"
    const val MODULE_DESCRIPTION = "network.xyo.module.description"
    const val MODULE_STATE = "network.xyo.module.state"

    // Manifest schemas - Section 7.4
    const val MANIFEST_PACKAGE = "network.xyo.manifest.package"
    const val MANIFEST_PACKAGE_DAPP = "network.xyo.manifest.package.dapp"
    const val MODULE_MANIFEST = "network.xyo.module.manifest"
    const val NODE_MANIFEST = "network.xyo.node.manifest"

    // Module filter/certification - Section 7.3
    const val MODULE_FILTER = "network.xyo.module.filter"
    const val CHILD_CERTIFICATION = "network.xyo.child.certification"
    const val ARCHIVIST_SNAPSHOT = "network.xyo.archivist.snapshot"
    const val ARCHIVIST_STATS = "network.xyo.archivist.stats"

    // Diviner type schemas - Section 7.5
    const val DIVINER_PAYLOAD = "network.xyo.diviner.payload"
    const val DIVINER_BOUNDWITNESS = "network.xyo.diviner.boundwitness"
    const val DIVINER_ADDRESS_HISTORY = "network.xyo.diviner.address.history"
    const val DIVINER_ADDRESS_CHAIN = "network.xyo.diviner.address.chain"
    const val DIVINER_ADDRESS_SPACE = "network.xyo.diviner.address.space"
    const val DIVINER_TRANSFORM = "network.xyo.diviner.transform"
    const val DIVINER_FORECASTING = "network.xyo.diviner.forecasting"
    const val DIVINER_JSONPATH = "network.xyo.diviner.jsonpath"
    const val DIVINER_JSONPATH_AGGREGATE = "network.xyo.diviner.jsonpath.aggregate"
    const val DIVINER_JSONPATCH = "network.xyo.diviner.jsonpatch"
    const val DIVINER_INDEXING = "network.xyo.diviner.indexing"
    const val DIVINER_PAYLOAD_POINTER = "network.xyo.diviner.payload.pointer"
    const val DIVINER_SCHEMA_LIST = "network.xyo.diviner.schema.list"
    const val DIVINER_BOUNDWITNESS_STATS = "network.xyo.diviner.boundwitness.stats"
    const val DIVINER_PAYLOAD_STATS = "network.xyo.diviner.payload.stats"
    const val DIVINER_SCHEMA_STATS = "network.xyo.diviner.schema.stats"

    // Automation schemas - Section 7.6
    const val AUTOMATION = "network.xyo.automation"
    const val AUTOMATION_INTERVAL = "network.xyo.automation.interval"
    const val AUTOMATION_EVENT_MODULE = "network.xyo.automation.event.module"

    // Data schemas - Section 7.7
    const val ID = "network.xyo.id"
    const val VALUE = "network.xyo.value"
    const val RANGE = "network.xyo.range"
    const val NUMBER = "network.xyo.number"
    const val BIGINT = "network.xyo.bigint"
}
