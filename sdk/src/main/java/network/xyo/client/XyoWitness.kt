package network.xyo.client

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.address.XyoAddress
import java.security.MessageDigest

open class XyoWitness<out T: XyoPayload>(
    val address: XyoAddress,
    val observer: ((context: Context, previousHash: String) -> T?)? = null,
    var previousHash: String = ""
) {

    constructor(
        observer: ((context: Context, previousHash: String) -> T?)?,
        previousHash: String = ""
    ): this(XyoAddress(), observer, previousHash)

    open fun observe(context: Context): T? {
        observer?.let {
            val payload = it(context, previousHash)
            payload?.let {
                previousHash = XyoSerializable.sha256String(it)
            }
            return payload
        }
        return null
    }
}