package network.xyo.client.witness.system.info

import android.content.Context
import network.xyo.client.XyoWitness

class XyoSystemInfoWitness: XyoWitness<XyoSystemInfoPayload>(
    fun (context: Context, _previousHash: String?): XyoSystemInfoPayload {
        return XyoSystemInfoPayload.detect(context)
    })