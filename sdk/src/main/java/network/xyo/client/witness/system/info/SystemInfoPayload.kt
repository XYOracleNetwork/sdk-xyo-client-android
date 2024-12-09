package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonClass
import network.xyo.client.payload.Payload

@JsonClass(generateAdapter = true)
class SystemInfoPayload(
    val device: XyoSystemInfoDevice? = null,
    val network: XyoSystemInfoNetwork? = null,
    val os: XyoSystemInfoOs? = null
): Payload (SCHEMA) {
    companion object {
        const val SCHEMA = "network.xyo.system.info.android"

        @RequiresApi(Build.VERSION_CODES.M)
        fun detect(context: Context): SystemInfoPayload {
            return SystemInfoPayload(
                XyoSystemInfoDevice.detect(context),
                XyoSystemInfoNetwork.detect(context),
                XyoSystemInfoOs.detect(context)
            )
        }
    }
    }