package network.xyo.client

class XyoSystemInfoWitness(): XyoWitness<XyoSystemInfoPayload> (fun (previousHash: String?): XyoSystemInfoPayload {
    val payload = XyoSystemInfoPayload()
    return payload
})