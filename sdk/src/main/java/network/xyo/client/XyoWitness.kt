package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.address.XyoAccount
import network.xyo.client.payload.XyoPayload

@RequiresApi(Build.VERSION_CODES.M)
open class XyoWitness<out T: XyoPayload> (
    val address: XyoAccount = XyoAccount(),
    private val observer: ((context: Context, previousHash: String) -> T?)? = null,
    var previousHash: String = ""
) {

    constructor(
        observer: ((context: Context, previousHash: String) -> T?)?,
        previousHash: String = "",
        account: XyoAccount = XyoAccount()
    ): this(account, observer, previousHash)

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