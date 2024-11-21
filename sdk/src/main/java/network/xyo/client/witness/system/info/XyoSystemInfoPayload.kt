package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.XyoPayload

@JsonClass(generateAdapter = true)
class XyoSystemInfoPayload(
    val device: XyoSystemInfoDevice? = null,
    val network: XyoSystemInfoNetwork? = null,
    val os: XyoSystemInfoOs? = null,
    ): XyoPayload () {
        override var schema: String
            get() = XyoSystemInfoPayload.schema
            set(value) = Unit

        companion object {
            val schema = "network.xyo.system.info.android"

            @RequiresApi(Build.VERSION_CODES.M)
            fun detect(context: Context): XyoSystemInfoPayload {
                return XyoSystemInfoPayload(
                    XyoSystemInfoDevice.detect(context),
                    XyoSystemInfoNetwork.detect(context),
                    XyoSystemInfoOs.detect(context)
                )
            }
        }
    }