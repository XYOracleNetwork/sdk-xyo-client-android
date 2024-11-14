package network.xyo.client.account

import network.xyo.client.account.model.AccountInstance
import network.xyo.client.account.model.AccountStatic
import network.xyo.client.account.model.PreviousHashStore
import tech.figure.hdwallet.ec.PrivateKey
import tech.figure.hdwallet.ec.secp256k1Curve
import tech.figure.hdwallet.signer.ASN1Signature
import tech.figure.hdwallet.signer.BCECSigner
import tech.figure.hdwallet.signer.ECDSASignature
import tech.figure.hdwallet.wallet.Account as TFAccount

open class Account(private val _account: TFAccount, private var _previousHash: ByteArray? = null): AccountInstance {

    constructor(privateKey: PrivateKey, previousHash: ByteArray? = null) : this(TFAccount.fromBip32("", toBase58(privateKey.key)), previousHash) {}
    constructor(privateKey: ByteArray, previousHash: ByteArray? = null) : this(TFAccount.fromBip32("", toBase58(privateKey)), previousHash) {}

    private val _address = hexStringToByteArray(_account.keyPair.publicKey.address("").value)

    override val address: ByteArray
        get() = _address
    override val previousHash: ByteArray?
        get() = _previousHash
    override val privateKey: ByteArray
        get() = _account.keyPair.privateKey.key.toByteArray()
    override val publicKey: ByteArray
        get() = _account.keyPair.publicKey.key.toByteArray()

    override fun sign(hash: ByteArray): ByteArray {
        val result = BCECSigner().sign(_account.keyPair.privateKey, hash)
        _previousHash = hash
        return result.encodeAsASN1DER().toByteArray()
    }

    override fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        return BCECSigner().verify(_account.keyPair.publicKey, msg, ECDSASignature.Companion.decode(
            ASN1Signature.fromByteArray(signature)))
    }

    companion object: AccountStatic<AccountInstance> {
        override var previousHashStore: PreviousHashStore? = null

        @OptIn(ExperimentalStdlibApi::class)
        override fun fromPrivateKey(key: ByteArray): AccountInstance {
            return Account(PrivateKey.fromBytes(key, secp256k1Curve))
        }

        override fun random(): AccountInstance {
            TODO("Not yet implemented")
        }
    }
}

