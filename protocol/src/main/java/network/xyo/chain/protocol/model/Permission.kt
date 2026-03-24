package network.xyo.chain.protocol.model

import com.squareup.moshi.JsonClass

enum class CaveatType {
    chain, expiration, filteredResponse, rateLimit, restrictReturnedAccounts
}

@JsonClass(generateAdapter = true)
data class Caveat(
    val type: String,
    val value: Any?,
)

@JsonClass(generateAdapter = true)
data class Permission(
    val parentCapability: String,
    val caveats: List<Caveat>? = null,
    val invoker: String,
)

@JsonClass(generateAdapter = true)
data class InvokerPermission(
    val parentCapability: String,
    val caveats: List<Caveat>? = null,
    val invoker: String,
    val date: Long? = null,
)

@JsonClass(generateAdapter = true)
data class RequestedPermission(
    val parentCapability: String,
    val date: Long? = null,
)
