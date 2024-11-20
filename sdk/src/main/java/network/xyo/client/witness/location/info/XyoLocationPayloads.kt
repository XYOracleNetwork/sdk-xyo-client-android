package network.xyo.client.witness.location.info

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class XyoLocationPayloads {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        suspend fun detect(context: Context): Pair<XyoLocationPayload, XyoLocationPayloadRaw>? {
            return XyoLocationCurrent.detect(context)
        }
    }
}