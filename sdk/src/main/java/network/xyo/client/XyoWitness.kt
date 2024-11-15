package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.address.XyoAccount
import network.xyo.client.payload.XyoPayload

abstract class DeferredObserver<out T: XyoPayload> {
    suspend abstract fun deferredDetect(context: Context, previousHash: String?): T?
}

@RequiresApi(Build.VERSION_CODES.M)
open class XyoWitness<out T: XyoPayload> (
    val address: AccountInstance = XyoAccount(),
    private val observer: ((context: Context, previousHash: String) -> T?)? = null,
    var previousHash: String = "",
    val deferredObserver: DeferredObserver<T>? = null
) {

    constructor(
        observer: ((context: Context, previousHash: String) -> T?)?,
        previousHash: String = "",
        account: AccountInstance = XyoAccount()
    ): this(account, observer, previousHash, null)

    constructor(
        observer: DeferredObserver<T>?,
        previousHash: String = "",
        account: AccountInstance = XyoAccount()
    ): this(account, null, previousHash, observer)

    open suspend fun observe(context: Context): T? {
        if (deferredObserver !== null) {
            val payload = deferredObserver.deferredDetect(context, previousHash)
            return payload
        }
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