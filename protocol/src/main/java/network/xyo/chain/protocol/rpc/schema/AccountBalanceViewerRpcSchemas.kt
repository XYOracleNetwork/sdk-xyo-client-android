package network.xyo.chain.protocol.rpc.schema

import com.squareup.moshi.Types
import network.xyo.chain.protocol.model.AccountBalanceHistoryItem
import network.xyo.chain.protocol.model.ChainQualified
import network.xyo.chain.protocol.model.ChainQualification
import network.xyo.chain.protocol.model.XL1BlockRange
import network.xyo.chain.protocol.rpc.types.RpcMethodNames
import network.xyo.chain.protocol.xl1.AttoXL1
import network.xyo.chain.protocol.block.XL1BlockNumber
import java.math.BigInteger

val AccountBalanceViewerRpcSchemas: RpcSchemaMap = rpcSchemaMap {
    method<ChainQualified<Map<String, AttoXL1>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_BALANCES) { raw ->
        parseQualifiedBalances(raw)
    }
    method<ChainQualified<Map<String, List<AccountBalanceHistoryItem>>>>(RpcMethodNames.ACCOUNT_BALANCE_VIEWER_QUALIFIED_HISTORIES) { raw ->
        parseQualifiedHistories(raw)
    }
}

private fun parseQualifiedBalances(raw: Any?): ChainQualified<Map<String, AttoXL1>> {
    val (data, qualification) = parseChainQualifiedTuple(raw)
    val balances = (data as? Map<*, *>).orEmpty().mapNotNull { (address, value) ->
        val addressKey = address as? String ?: return@mapNotNull null
        addressKey to parseAttoXL1(value)
    }.toMap()
    return ChainQualified(balances, qualification)
}

private fun parseQualifiedHistories(raw: Any?): ChainQualified<Map<String, List<AccountBalanceHistoryItem>>> {
    val (data, qualification) = parseChainQualifiedTuple(raw)
    val historyType = Types.newParameterizedType(
        List::class.java,
        AccountBalanceHistoryItem::class.java,
    )

    val histories = (data as? Map<*, *>).orEmpty().mapNotNull { (address, value) ->
        val addressKey = address as? String ?: return@mapNotNull null
        val parsedHistory = rpcMoshi.adapter<List<AccountBalanceHistoryItem>>(historyType).fromJsonValue(value)
            ?: emptyList()
        addressKey to parsedHistory
    }.toMap()

    return ChainQualified(histories, qualification)
}

private fun parseChainQualifiedTuple(raw: Any?): Pair<Any?, ChainQualification> {
    val tuple = raw as? List<*> ?: throw IllegalArgumentException("Expected [data, qualification] tuple")
    val data = tuple.getOrNull(0)
    val qualificationRaw = tuple.getOrNull(1) as? Map<*, *>
        ?: throw IllegalArgumentException("Expected qualification object in tuple position 1")
    return data to parseChainQualification(qualificationRaw)
}

private fun parseChainQualification(raw: Map<*, *>): ChainQualification {
    val head = raw["head"]?.toString()
        ?: throw IllegalArgumentException("Expected qualification.head")
    val rangeValues = raw["range"] as? List<*>
        ?: throw IllegalArgumentException("Expected qualification.range")
    if (rangeValues.size != 2) throw IllegalArgumentException("Expected qualification.range to have 2 entries")

    val start = parseBlockNumber(rangeValues[0])
    val end = parseBlockNumber(rangeValues[1])
    return ChainQualification(head, XL1BlockRange(start, end))
}

private fun parseBlockNumber(raw: Any?): XL1BlockNumber {
    val value = when (raw) {
        is Number -> raw.toLong()
        else -> raw?.toString()?.toLongOrNull()
    } ?: throw IllegalArgumentException("Expected block number, got: $raw")

    return XL1BlockNumber(value)
}

private fun parseAttoXL1(raw: Any?): AttoXL1 {
    val value = when (raw) {
        null -> BigInteger.ZERO
        is String -> parseBigIntegerString(raw)
        is Number -> BigInteger.valueOf(raw.toLong())
        is BigInteger -> raw
        is Map<*, *> -> parseAttoXL1(raw["value"]).value
        else -> parseBigIntegerString(raw.toString())
    }
    return AttoXL1(value)
}

private fun parseBigIntegerString(raw: String): BigInteger {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return BigInteger.ZERO

    return if (trimmed.startsWith("0x", ignoreCase = true)) {
        val hex = trimmed.removePrefix("0x").removePrefix("0X")
        if (hex.isEmpty()) BigInteger.ZERO else BigInteger(hex, 16)
    } else {
        BigInteger(trimmed)
    }
}
