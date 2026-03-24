package network.xyo.chain.protocol.model

import network.xyo.chain.protocol.block.XL1BlockNumber

data class XL1BlockRange(
    val start: XL1BlockNumber,
    val end: XL1BlockNumber,
) {
    init {
        require(start <= end) { "Block range start must be <= end" }
    }

    val span: Long get() = end - start

    fun toKey(): String = "${start.value}|${end.value}"

    companion object {
        fun fromKey(key: String): XL1BlockRange? {
            val parts = key.split("|")
            if (parts.size != 2) return null
            val start = parts[0].toLongOrNull() ?: return null
            val end = parts[1].toLongOrNull() ?: return null
            return runCatching { XL1BlockRange(XL1BlockNumber(start), XL1BlockNumber(end)) }.getOrNull()
        }
    }
}
