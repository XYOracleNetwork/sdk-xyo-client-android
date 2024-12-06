package network.xyo.client.node.client

import network.xyo.client.lib.JsonSerializable

class PostQueryResult(
    val response: QueryResponseWrapper?,
    val errors: ArrayList<Error>?,
) : JsonSerializable(){
    operator fun component1() = response
    operator fun component2() = errors
}