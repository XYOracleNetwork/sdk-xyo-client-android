package network.xyo.client.witness

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.Account
import network.xyo.client.payload.Payload

abstract class DeferredObserver<out T: Payload> {
    abstract suspend fun deferredDetect(context: Context): List<T>?
}

@RequiresApi(Build.VERSION_CODES.M)
open class XyoWitness<out T: Payload> (
    val address: network.xyo.client.account.model.Account = Account.random(),
    private val observer: ((context: Context) -> List<T>?)? = null,
    val deferredObserver: DeferredObserver<T>? = null
) {

    constructor(
        observer: ((context: Context) -> List<T>?)?,
        account: network.xyo.client.account.model.Account = Account.random()
    ): this(account, observer)

    constructor(
        observer: DeferredObserver<T>?,
        account: network.xyo.client.account.model.Account = Account.random()
    ): this(account, null, observer)

    open suspend fun observe(context: Context): List<T>? {
        val appContext = context.applicationContext
        if (deferredObserver !== null) {
            val payload = deferredObserver.deferredDetect(appContext)
            return payload
        }
        observer?.let {
            val payloads = it(appContext)
            return payloads
        }
        return null
    }
}