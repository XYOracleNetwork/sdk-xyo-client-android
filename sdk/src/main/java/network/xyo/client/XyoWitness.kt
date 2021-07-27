package network.xyo.client

import android.content.Context
import network.xyo.client.address.XyoAddress

open class XyoWitness<out T: XyoPayload>(
    val address: XyoAddress,
    val observer: ((context: Context, previousHash: String?) -> T?)? = null,
    var previousHash: String? = null
) {

    constructor(
        observer: ((context: Context, previousHash: String?) -> T?)?,
        previousHash: String? = null
    ): this(XyoAddress(), observer, previousHash)

    open fun observe(context: Context): T? {
        observer?.let {
            val payload = it(context, previousHash)
            previousHash = payload?.sha256()
            return payload
        }
        return null
    }
}