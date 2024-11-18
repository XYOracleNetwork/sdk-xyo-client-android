package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.Account
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.XyoPayload

abstract class DeferredObserver<out T: XyoPayload> {
    abstract suspend fun deferredDetect(context: Context, previousHash: String?): List<T>?
}

@RequiresApi(Build.VERSION_CODES.M)
open class XyoWitness<out T: XyoPayload> (
    val address: AccountInstance = Account.random(),
    private val observer: ((context: Context, previousHash: String) -> List<T>?)? = null,
    var previousHash: String = "",
    val deferredObserver: DeferredObserver<T>? = null
) {

    constructor(
        observer: ((context: Context, previousHash: String) -> List<T>?)?,
        previousHash: String = "",
        account: AccountInstance = Account.random()
    ): this(account, observer, previousHash, null)

    constructor(
        observer: DeferredObserver<T>?,
        previousHash: String = "",
        account: AccountInstance = Account.random()
    ): this(account, null, previousHash, observer)

    open suspend fun observe(context: Context): List<T>? {
        val appContext = context.applicationContext
        if (deferredObserver !== null) {
            val payload = deferredObserver.deferredDetect(appContext, previousHash)
            return payload
        }
        observer?.let {
            val payloads = it(appContext, previousHash)
            return payloads
        }
        return null
    }
}