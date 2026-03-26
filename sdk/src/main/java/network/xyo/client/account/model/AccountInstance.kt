package network.xyo.client.account.model

/**
 * Core account interface matching JS AccountInstance.
 *
 * Combines signing (PrivateKey), verification (PublicKey), and key management
 * into a single interface. This is the primary interface name matching the JS SDK.
 */
interface AccountInstance : PrivateKey {
    val previousHash: ByteArray?
    val privateKey: ByteArray
    val publicKey: ByteArray
    val publicKeyUncompressed: ByteArray
}
