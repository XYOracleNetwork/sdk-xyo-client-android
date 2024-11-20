package network.xyo.client.node.client

import network.xyo.client.lib.XyoSerializable

class PostQueryResult(
    val response: QueryResponseWrapper?,
    val errors: ArrayList<Error>?,
) : XyoSerializable(){
    operator fun component1() = response
    operator fun component2() = errors
}