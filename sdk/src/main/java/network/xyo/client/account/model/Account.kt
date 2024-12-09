package network.xyo.client.account.model

interface Account: PrivateKey {
    val previousHash: ByteArray?
    val privateKey: ByteArray
    val publicKey: ByteArray
    val publicKeyUncompressed: ByteArray
}