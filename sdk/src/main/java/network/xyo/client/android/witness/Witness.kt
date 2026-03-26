package network.xyo.client.android.witness

import network.xyo.client.payload.Payload

/**
 * Non-prefixed alias for [XyoWitness], matching JS SDK naming convention.
 * New code should use this name.
 */
typealias Witness<T> = XyoWitness<T>

/**
 * Non-prefixed alias for [XyoPanelReportQueryResult].
 */
typealias PanelReportQueryResult = XyoPanelReportQueryResult

/**
 * Non-prefixed alias for [XyoPanel], matching JS SDK naming convention.
 * Note: In the JS SDK, this role is fulfilled by Sentinel.
 * New code should use this name.
 */
typealias Panel = XyoPanel
