package network.xyo.client.android.witness.location.info

import android.annotation.SuppressLint
import android.content.Context

class XyoLocationPayloads {
    companion object {

        @SuppressLint("MissingPermission")
        suspend fun detect(context: Context): Pair<LocationPayload, LocationPayloadRaw>? {
            return XyoLocationCurrent.detect(context)
        }
    }
}
