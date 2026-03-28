package network.xyo.client.sentinel.model

import network.xyo.client.module.model.ModuleConfig

/**
 * Sentinel-specific configuration.
 * Per the XYO Yellow Paper Section 11.4.
 */
interface SentinelConfig : ModuleConfig {
    /** Task graph defining the execution pipeline. */
    val tasks: List<SentinelTask>?
        get() = null

    /** If true, report() awaits all tasks. If false, fires and forgets. */
    val synchronous: Boolean?
        get() = true

    /** If true, report() throws on error. */
    val throwErrors: Boolean?
        get() = true

    companion object {
        const val SCHEMA = "network.xyo.sentinel.config"
    }
}

/**
 * A task in the sentinel pipeline.
 */
data class SentinelTask(
    /** Module identifier to invoke. */
    val mod: String,
    /** Which previous task(s) provide input. */
    val input: Any? = null
)
