package network.xyo.client

import network.xyo.client.address.XyoAddress

open class XyoWitness<out T: XyoPayload>(
    val address: XyoAddress,
    val observer: ((previousHash: String?) -> T?)? = null,
    var previousHash: String? = null
) {

    constructor(observer: ((previousHash: String?) -> T?), previousHash: String? = null): this(XyoAddress(), observer, previousHash)

    open fun observe(): T? {
        observer?.let {
            val payload = it(previousHash)
            previousHash = payload?.sha256()
            return payload
        }
        return null
    }
}