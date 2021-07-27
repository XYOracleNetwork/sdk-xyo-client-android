package network.xyo.client.witness.system.info

import android.content.Context
import network.xyo.client.XyoPayload

class XyoSystemInfoPayload(
    val device: XyoSystemInfoDevice? = null,
    val network: XyoSystemInfoNetwork? = null,
    val os: XyoSystemInfoOs? = null
    ): XyoPayload("network.xyo.system.info") {
        companion object {
            fun detect(context: Context): XyoSystemInfoPayload {
                return XyoSystemInfoPayload(
                    XyoSystemInfoDevice.detect(context),
                    XyoSystemInfoNetwork.detect(context),
                    XyoSystemInfoOs.detect(context)
                )
            }
        }
    }