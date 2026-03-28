package network.xyo.client.module

/**
 * Standard query schema constants matching the XYO Yellow Paper Section 6.5.
 */
object QuerySchemas {

    // Module (base) queries - Section 6.5.1
    const val MODULE_MANIFEST = "network.xyo.query.module.manifest"
    const val MODULE_ADDRESS = "network.xyo.query.module.address"
    const val MODULE_STATE = "network.xyo.query.module.state"
    const val MODULE_SUBSCRIBE = "network.xyo.query.module.subscribe"

    // Archivist queries - Section 6.5.2
    const val ARCHIVIST_INSERT = "network.xyo.query.archivist.insert"
    const val ARCHIVIST_GET = "network.xyo.query.archivist.get"
    const val ARCHIVIST_DELETE = "network.xyo.query.archivist.delete"
    const val ARCHIVIST_ALL = "network.xyo.query.archivist.all"
    const val ARCHIVIST_CLEAR = "network.xyo.query.archivist.clear"
    const val ARCHIVIST_COMMIT = "network.xyo.query.archivist.commit"
    const val ARCHIVIST_SNAPSHOT = "network.xyo.query.archivist.snapshot"
    const val ARCHIVIST_NEXT = "network.xyo.query.archivist.next"

    // Diviner queries - Section 6.5.3
    const val DIVINER_DIVINE = "network.xyo.query.diviner.divine"

    // Witness queries - Section 6.5.4
    const val WITNESS_OBSERVE = "network.xyo.query.witness.observe"

    // Sentinel queries - Section 6.5.5
    const val SENTINEL_REPORT = "network.xyo.query.sentinel.report"

    // Bridge queries - Section 6.5.6
    const val BRIDGE_CONNECT = "network.xyo.query.bridge.connect"
    const val BRIDGE_DISCONNECT = "network.xyo.query.bridge.disconnect"
    const val BRIDGE_EXPOSE = "network.xyo.query.bridge.expose"
    const val BRIDGE_UNEXPOSE = "network.xyo.query.bridge.unexpose"

    // Node queries - Section 6.5.7
    const val NODE_ATTACH = "network.xyo.query.node.attach"
    const val NODE_DETACH = "network.xyo.query.node.detach"
    const val NODE_CERTIFY = "network.xyo.query.node.certify"
    const val NODE_ATTACHED = "network.xyo.query.node.attached"
    const val NODE_REGISTERED = "network.xyo.query.node.registered"
}
