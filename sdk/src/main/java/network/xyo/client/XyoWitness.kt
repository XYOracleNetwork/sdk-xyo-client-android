package network.xyo.client

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.address.XyoAccount
import network.xyo.client.boundwitness.XyoBoundWitnessBuilder
import network.xyo.client.boundwitness.XyoBoundWitnessJson
import network.xyo.client.module.ModuleQueryResult
import network.xyo.client.payload.XyoPayload

@RequiresApi(Build.VERSION_CODES.M)
open class XyoWitness<out T: XyoPayload> constructor(
    val address: XyoAccount = XyoAccount(),
    val observer: ((context: Context, previousHash: String) -> T?)? = null,
    var previousHash: String = ""
) {

    constructor(
        observer: ((context: Context, previousHash: String) -> T?)?,
        previousHash: String = "",
        account: XyoAccount = XyoAccount()
    ): this(account, observer, previousHash)

    open fun observe(context: Context): ModuleQueryResult<T>? {
        observer?.let {
            val payload = it(context, previousHash)
            payload?.let { notNullPayload ->
                previousHash = XyoSerializable.sha256String(notNullPayload)
            }

            val bw = XyoBoundWitnessBuilder().let { boundWitnessBuilder ->
                payload?.let {
                    boundWitnessBuilder.payloads(listOf(it))
                        .witness(address, previousHash)
                        .build()
                }
            }

            val returning = bw?.let {
                payload?.let { notNullPayload ->
                    Triple(it, listOf(notNullPayload), listOf<Exception>())
                }
            }

            if (returning !== null) {
                return returning
            } else {
                return null
            }
        }
        return null
    }
}